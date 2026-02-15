import { apiClient } from "../api-client"
import { API_CONFIG } from "../api-config"

export interface WalletTransaction {
    id: string
    amount: number
    type: 'CREDIT' | 'DEBIT'
    description: string
    date: string
    referenceId?: string
}

export interface WalletResponse {
    walletId: string
    balance: number // Total balance
    purchasedCredits: number
    bonusCredits: number
    transactions?: WalletTransaction[]
}

export interface PackageResponse {
    id: string
    name: string
    credits: number
    bonus: number
    price: number
    description: string
}

export interface PaymentOrderResponse {
    orderId: string // Razorpay order ID
    amount: number
    currency: string
    key: string
}

export const walletService = {
    getWalletDetails: async () => {
        return apiClient.get<WalletResponse>(API_CONFIG.ENDPOINTS.WALLET.GET_BALANCE)
    },

    getPackages: async () => {
        return apiClient.get<PackageResponse[]>(API_CONFIG.ENDPOINTS.WALLET.GET_PACKAGES)
    },

    initiateRecharge: async (packageId: string) => {
        return apiClient.post<PaymentOrderResponse>(API_CONFIG.ENDPOINTS.WALLET.INITIATE_RECHARGE, { packageId })
    },

    verifyPayment: async (paymentData: any) => {
        return apiClient.post<WalletResponse>(API_CONFIG.ENDPOINTS.WALLET.VERIFY_PAYMENT, paymentData)
    }
}
