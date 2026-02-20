import { fetchWithMockFallback, apiClient } from "../api-client"
import { API_CONFIG } from "../api-config"

// Matches backend LeadDetailsResponse record exactly
export interface Lead {
    id: string
    customerName: string
    customerPhone: string | null    // null when lead is not yet purchased
    customerEmail: string | null    // null when lead is not yet purchased
    vehicleModel: string
    vehicleYear: string
    status: string                  // LeadStatus enum: NEW | BOUGHT | FOLLOW_UP | CONVERTED | SKIPPED
    leadCost: number
    purchasedByDealerId: string | null
    createdAt: string
    purchasedAt: string | null
}

export const leadService = {
    createLead: async (data: any) => {
        return apiClient.post(API_CONFIG.ENDPOINTS.CUSTOMER_LEADS.CREATE, data)
    },

    getCustomerLeads: async () => {
        return apiClient.get<{ leads: Lead[] }>(API_CONFIG.ENDPOINTS.CUSTOMER_LEADS.GET_ALL)
    },

    getLeadOffers: async (id: string) => {
        return apiClient.get<{ offers: any[] }>(API_CONFIG.ENDPOINTS.CUSTOMER_LEADS.GET_OFFERS(id))
    },

    selectOffer: async (leadId: string, dealerId: string) => {
        return apiClient.post(API_CONFIG.ENDPOINTS.CUSTOMER_LEADS.SELECT_OFFER(leadId, dealerId))
    }
}
