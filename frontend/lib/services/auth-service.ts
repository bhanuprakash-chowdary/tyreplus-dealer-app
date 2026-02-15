import { fetchWithMockFallback } from "../api-client"
import { API_CONFIG } from "../api-config"

// Define types for Auth API
// Define types for Auth API based on Backend DTOs
interface UserInfo {
    id: string
    name: string
    role: string
    avatar?: string
}

interface LoginResponse {
    token: string
    refreshToken: string
    user: UserInfo
}

interface OtpResponse {
    message: string
    otp?: string // Included in backend response for debug/dev
}

export const authService = {
    // Quick Auth (Vehicle Selector)
    sendQuickOtp: async (mobile: string) => {
        return fetchWithMockFallback<OtpResponse>(
            API_CONFIG.ENDPOINTS.AUTH.QUICK.SEND_OTP,
            {
                method: "POST",
                body: JSON.stringify({ mobile }),
            },
            { message: "OTP sent successfully" }
        )
    },

    verifyQuickOtp: async (mobile: string, otp: string) => {
        // Mock user for fallback
        const mockUser: UserInfo = {
            id: "user_mock_123",
            name: "Guest User",
            role: "customer"
        }

        return fetchWithMockFallback<LoginResponse>(
            API_CONFIG.ENDPOINTS.AUTH.QUICK.VERIFY_OTP,
            {
                method: "POST",
                body: JSON.stringify({ mobile, otp }),
            },
            { token: "mock_token_123", refreshToken: "mock_refresh_123", user: mockUser }
        )
    },

    // Full Login


    // Registration
    sendRegisterOtp: async (mobile: string) => {
        return fetchWithMockFallback<OtpResponse>(
            API_CONFIG.ENDPOINTS.AUTH.REGISTER.SEND_OTP,
            {
                method: "POST",
                body: JSON.stringify({ mobile }),
            },
            { message: "OTP sent successfully" }
        )
    },

    completeRegistration: async (data: any) => {
        const mockUser: UserInfo = {
            id: `user_${Date.now()}`,
            name: data.name || "New User",
            role: "dealer"
        }

        return fetchWithMockFallback<LoginResponse>(
            API_CONFIG.ENDPOINTS.AUTH.REGISTER.COMPLETE,
            {
                method: "POST",
                body: JSON.stringify(data),
            },
            { token: "mock_reg_token", refreshToken: "mock_refresh_reg", user: mockUser }
        )
    },
}
