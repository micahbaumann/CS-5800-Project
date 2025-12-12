package com.chachef.controller;

import com.chachef.dataobjects.AuthContext;
import com.chachef.dto.MessageReturnDto;
import com.chachef.dto.MessageSendDto;
import com.chachef.entity.MessageAccount;
import com.chachef.service.JwtService;
import com.chachef.service.MessageService;
import com.chachef.service.exceptions.InvalidUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService mockMessageService;

    @MockitoBean
    private JwtService mockJwtService;

    private final String username = "username";
    private final UUID uuid = UUID.randomUUID();

    // Standard test data
    private final UUID userId = UUID.randomUUID();
    private final UUID recipientId = UUID.randomUUID();
    private final UUID messageId = UUID.randomUUID();
    private final AuthContext mockAuthContext = new AuthContext(userId, "test@user.com", "USER");
    private final LocalDateTime now = LocalDateTime.now(); // Used for consistency in DTO mocking

    @BeforeEach
    public void setUp() {
        when(mockJwtService.getUsername(any(String.class))).thenReturn(username);
        when(mockJwtService.getUserId(any(String.class))).thenReturn(uuid);
        when(mockJwtService.getName(any(String.class))).thenReturn(username);
    }

    // Utility for creating the JSON payload for the MessageSendDto (using 'from', 'to', 'message')
    private String createMessageSendPayload() {
        return """
            {
              "from": "%s",
              "to": "%s",
              "message": "Hello, this is a test message for the new DTO."
            }
            """.formatted(userId.toString(), recipientId.toString());
    }

    /**
     * Corrected DTO mock creation to match the 5-argument constructor using LocalDateTime.
     */
    private MessageReturnDto createMockMessageReturnDto() {
        return new MessageReturnDto(
                messageId,
                userId,
                recipientId,
                "Test Content",
                now
        );
    }

    // --- TEST /message/send ---
    @Test
    @DisplayName("POST /send - Successful message send")
    void sendMessage_Success() throws Exception {
        String payload = createMessageSendPayload();

        doNothing().when(mockMessageService).sendMessage(any(AuthContext.class), any(MessageSendDto.class));

        mockMvc.perform(post("/message/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .requestAttr("auth", mockAuthContext)) // Inject AuthContext
                .andExpect(status().isNoContent()); // 204

        // Verify the service was called with the correct AuthContext and DTO content
        ArgumentCaptor<MessageSendDto> dtoCaptor = ArgumentCaptor.forClass(MessageSendDto.class);
        verify(mockMessageService, times(1)).sendMessage(any(AuthContext.class), dtoCaptor.capture());

        // Assert that the DTO fields were correctly parsed from the payload
        assertEquals(userId, dtoCaptor.getValue().getFrom());
        assertEquals(recipientId, dtoCaptor.getValue().getTo());
        assertEquals("Hello, this is a test message for the new DTO.", dtoCaptor.getValue().getMessage());
    }

    @Test
    @DisplayName("POST /send - Validation failure: Missing 'to' field (@NotNull)")
    void sendMessage_Validation_MissingToField() throws Exception {
        String invalidPayload = """
            {
              "from": "%s",
              "message": "Content here"
            }
            """.formatted(userId.toString());

        mockMvc.perform(post("/message/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload)
                        .requestAttr("auth", mockAuthContext))
                .andExpect(status().isBadRequest()); // 400 Bad Request

        verify(mockMessageService, never()).sendMessage(any(), any());
    }

    @Test
    @DisplayName("POST /send - Validation failure: Empty 'message' field (@NotBlank)")
    void sendMessage_Validation_EmptyMessageField() throws Exception {
        String invalidPayload = """
            {
              "from": "%s",
              "to": "%s",
              "message": "  "
            }
            """.formatted(userId.toString(), recipientId.toString());

        mockMvc.perform(post("/message/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload)
                        .requestAttr("auth", mockAuthContext))
                .andExpect(status().isBadRequest()); // 400 Bad Request

        verify(mockMessageService, never()).sendMessage(any(), any());
    }

    // --- TEST /message/list/user ---
    @Test
    @DisplayName("GET /list/user - Successful listing of user messages")
    void listUserMessages_Success() throws Exception {
        // ARRANGE: Setup mock service return value
        MessageReturnDto mockDto = createMockMessageReturnDto();
        Map<String, List<MessageReturnDto>> mockResponse = Map.of("messages", List.of(mockDto));

        when(mockMessageService.listUserMessages(any(AuthContext.class))).thenReturn(mockResponse);

        mockMvc.perform(get("/message/list/user")
                        .requestAttr("auth", mockAuthContext))
                .andExpect(status().isOk()) // 200
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messages[0].messageId").value(mockDto.getMessageId().toString()))
                .andExpect(jsonPath("$.messages[0].message").value(mockDto.getMessage())); // Changed content to message

        // Verify service called with correct AuthContext
        verify(mockMessageService, times(1)).listUserMessages(mockAuthContext);
    }

    // --- TEST /message/list/chef/{chefId} ---
    @Test
    @DisplayName("GET /list/chef/{chefId} - Successful listing of chef messages")
    void listChefMessages_Success() throws Exception {
        // ARRANGE: Setup mock service return value
        MessageReturnDto mockDto = createMockMessageReturnDto();
        Map<String, List<MessageReturnDto>> mockResponse = Map.of("messages", List.of(mockDto));

        when(mockMessageService.listChefMessages(any(AuthContext.class), any(UUID.class))).thenReturn(mockResponse);

        mockMvc.perform(get("/message/list/chef/{chefId}", recipientId)
                        .requestAttr("auth", mockAuthContext))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.messages[0].message").value(mockDto.getMessage())); // Changed content to message

        // Verify service called with correct AuthContext and chefId (using recipientId here)
        verify(mockMessageService, times(1)).listChefMessages(mockAuthContext, recipientId);
    }

    // --- TEST /message/view/{messageId} ---
    @Test
    @DisplayName("GET /view/{messageId} - Successful viewing of a single message")
    void viewMessage_Success() throws Exception {
        // ARRANGE: Setup mock service return value
        MessageReturnDto mockDto = createMockMessageReturnDto();
        when(mockMessageService.viewMessage(any(AuthContext.class), any(UUID.class))).thenReturn(mockDto);

        mockMvc.perform(get("/message/view/{messageId}", messageId)
                        .requestAttr("auth", mockAuthContext))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.messageId").value(messageId.toString()))
                .andExpect(jsonPath("$.message").value(mockDto.getMessage())); // Changed content to message

        // Verify service called with correct AuthContext and messageId
        verify(mockMessageService, times(1)).viewMessage(mockAuthContext, messageId);
    }

    // --- TEST /message/user/{userId}/message-account ---
    @Test
    @DisplayName("GET /user/{userId}/message-account - Successful retrieval/creation of user account")
    void userMessageAccount_Success() throws Exception {
        // ARRANGE: Setup mock MessageAccount
        UUID accountId = UUID.randomUUID();
        MessageAccount mockAccount = new MessageAccount();
        mockAccount.setMessageAccountId(accountId);

        when(mockMessageService.getCreateMessageAccountUser(any(UUID.class))).thenReturn(mockAccount);

        mockMvc.perform(get("/message/user/{userId}/message-account", userId))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.message_account_id").value(accountId.toString()));

        // Verify service called with correct userId
        verify(mockMessageService, times(1)).getCreateMessageAccountUser(userId);
    }


    @Test
    @DisplayName("GET /user/{userId}/message-account - Handles InvalidUserException")
    void userMessageAccount_InvalidUserException() throws Exception {
        String errorMessage = "User role not supported for message account.";

        doThrow(new InvalidUserException(errorMessage))
                .when(mockMessageService).getCreateMessageAccountUser(any(UUID.class));

        mockMvc.perform(get("/message/user/{userId}/message-account", userId))
                .andExpect(status().is(409)); // assuming your @ControllerAdvice maps InvalidUserException to 409

        verify(mockMessageService, times(1)).getCreateMessageAccountUser(userId);
    }

    @Test
    @DisplayName("GET /user/{userId}/message-account - Handles generic Exception (InternalAppError)")
    void userMessageAccount_GenericException() throws Exception {

        doThrow(new RuntimeException("Database connection failure"))
                .when(mockMessageService).getCreateMessageAccountUser(any(UUID.class));

        mockMvc.perform(get("/message/user/{userId}/message-account", userId))
                .andExpect(status().isInternalServerError()); // assuming InternalAppError -> 500

        verify(mockMessageService, times(1)).getCreateMessageAccountUser(userId);
    }

    // --- TEST /message/chef/{chefId}/message-account ---
    @Test
    @DisplayName("GET /chef/{chefId}/message-account - Successful retrieval/creation of chef account")
    void chefMessageAccount_Success() throws Exception {
        // ARRANGE: Setup mock MessageAccount
        UUID accountId = UUID.randomUUID();
        MessageAccount mockAccount = new MessageAccount();
        mockAccount.setMessageAccountId(accountId);

        when(mockMessageService.getCreateMessageAccount(any(UUID.class))).thenReturn(mockAccount);

        mockMvc.perform(get("/message/chef/{chefId}/message-account", recipientId))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.message_account_id").value(accountId.toString()));

        // Verify service called with correct chefId
        verify(mockMessageService, times(1)).getCreateMessageAccount(recipientId);
    }

    @Test
    @DisplayName("GET /chef/{chefId}/message-account - Handles InvalidUserException")
    void chefMessageAccount_InvalidUserException() throws Exception {
        String errorMessage = "Chef ID not found.";

        doThrow(new InvalidUserException(errorMessage))
                .when(mockMessageService).getCreateMessageAccount(any(UUID.class));

        mockMvc.perform(get("/message/chef/{chefId}/message-account", recipientId))
                .andExpect(status().is(409)); // assuming InvalidUserException -> 409

        verify(mockMessageService, times(1)).getCreateMessageAccount(recipientId);
    }

    @Test
    @DisplayName("GET /chef/{chefId}/message-account - Handles generic Exception (InternalAppError)")
    void chefMessageAccount_GenericException() throws Exception {

        doThrow(new NullPointerException("NPE in service"))
                .when(mockMessageService).getCreateMessageAccount(any(UUID.class));

        mockMvc.perform(get("/message/chef/{chefId}/message-account", recipientId))
                .andExpect(status().isInternalServerError()); // assuming InternalAppError -> 500

        verify(mockMessageService, times(1)).getCreateMessageAccount(recipientId);
    }
}