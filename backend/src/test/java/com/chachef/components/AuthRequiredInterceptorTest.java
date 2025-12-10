package com.chachef.components;

import com.chachef.annotations.RequireAuth;
import com.chachef.dataobjects.AuthContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthRequiredInterceptorTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    private final AuthRequiredInterceptor interceptor = new AuthRequiredInterceptor();

    static class NoAuthController {
        public void noAuthMethod() {}
    }

    static class MethodAuthController {
        @RequireAuth
        public void methodProtected() {}
    }

    @RequireAuth
    static class ClassAuthController {
        public void classProtected() {}
        public void anotherProtected() {}
    }

    @Test
    void preHandle_nonHandlerMethod_returnsTrue() throws Exception {
        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verifyNoInteractions(response);
    }

    @Test
    void preHandle_noRequireAuthAnnotation_returnsTrue() throws Exception {
        Method method = NoAuthController.class.getMethod("noAuthMethod");
        HandlerMethod handler = new HandlerMethod(new NoAuthController(), method);

        boolean result = interceptor.preHandle(request, response, handler);

        assertTrue(result);
        verifyNoInteractions(response);
    }

    @Test
    void preHandle_methodLevelRequireAuth_noAuthContext_returns401AndFalse() throws Exception {
        Method method = MethodAuthController.class.getMethod("methodProtected");
        HandlerMethod handler = new HandlerMethod(new MethodAuthController(), method);

        when(request.getAttribute("auth")).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, handler);

        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void preHandle_methodLevelRequireAuth_authContextPresent_returnsTrue() throws Exception {
        Method method = MethodAuthController.class.getMethod("methodProtected");
        HandlerMethod handler = new HandlerMethod(new MethodAuthController(), method);

        AuthContext auth = new AuthContext(UUID.randomUUID(), "alice", "Alice Doe");
        when(request.getAttribute("auth")).thenReturn(auth);

        boolean result = interceptor.preHandle(request, response, handler);

        assertTrue(result);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void preHandle_classLevelRequireAuth_noAuthContext_returns401AndFalse() throws Exception {
        Method method = ClassAuthController.class.getMethod("classProtected");
        HandlerMethod handler = new HandlerMethod(new ClassAuthController(), method);

        when(request.getAttribute("auth")).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, handler);

        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void preHandle_classLevelRequireAuth_authContextPresent_returnsTrue() throws Exception {
        Method method = ClassAuthController.class.getMethod("anotherProtected");
        HandlerMethod handler = new HandlerMethod(new ClassAuthController(), method);

        AuthContext auth = new AuthContext(UUID.randomUUID(), "alice", "Alice Doe");
        when(request.getAttribute("auth")).thenReturn(auth);

        boolean result = interceptor.preHandle(request, response, handler);

        assertTrue(result);
        verify(response, never()).setStatus(anyInt());
    }
}
