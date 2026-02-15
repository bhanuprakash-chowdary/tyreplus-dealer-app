export const API_CONFIG = {
    // Direct API call
    BASE_URL: process.env.NEXT_PUBLIC_API_URL || "/api/v1",
    MOCK_MODE: false,
    ENDPOINTS: {
        AUTH: {
            QUICK: {
                SEND_OTP: "/auth/quick/send-otp",
                VERIFY_OTP: "/auth/quick/verify-otp",
            },

            REGISTER: {
                SEND_OTP: "/auth/register/send-otp",
                COMPLETE: "/auth/register/complete",
            },
            PASSWORD_RESET: {
                SEND_OTP: "/auth/password-reset/send-otp",
                VERIFY_OTP: "/auth/password-reset/verify-otp",
                CONFIRM: "/auth/password-reset/confirm",
            },
        },
        USER: {
            PROFILE: "/dealer/profile",
        },
        VEHICLES: {
            MAKES: "/vehicles/makes",
            MODELS: "/vehicles/models",
            VARIANTS: "/vehicles/variants",
            // Keep generic ones for now if used elsewhere
            GET_ALL: "/vehicles",
            ADD: "/vehicles",
            DELETE: (id: string) => `/vehicles/${id}`,
        },
        LOCATION: {
            CHECK_PINCODE: "/locations/check",
        },
        WALLET: {
            GET_BALANCE: "/dealer/wallet",
            GET_PACKAGES: "/dealer/packages",
            INITIATE_RECHARGE: "/dealer/recharge/initiate",
            VERIFY_PAYMENT: "/dealer/recharge/verify",
        },
        SELL_TYRES: {
            SUBMIT: "/sell-tyres/submit",
        },
        TYRES: {
            GET_ALL: "/tyres",
            GET_BY_ID: (id: string) => `/tyres/${id}`,
        },
        ORDERS: {
            GET_ALL: "/orders",
            GET_BY_ID: (id: string) => `/orders/${id}`,
            CREATE: "/orders",
        },
        REQUESTS: {
            GET_ALL: "/orders/requests",
            CREATE: "/orders/requests",
        },
        LEADS: {
            GET_ALL: "/leads",
            GET_BY_ID: (id: string) => `/leads/${id}`,
            BUY: (id: string) => `/leads/${id}/buy`,
            SKIP: (id: string) => `/leads/${id}/skip`,
            UPDATE_STATUS: (id: string) => `/leads/${id}/status`,
        },
    },
}
