import { fetchWithMockFallback, apiClient } from "../api-client"
import { API_CONFIG } from "../api-config"

// Define Lead types based on LeadDetailsResponse
export interface Lead {
    id: string
    name: string // Customer name? Or Lead source?
    phone: string
    status: string // LeadStatus enum string
    vehicle?: string
    tyreRequirement?: string
    location?: string
    price?: number // Cost to buy lead?
    createdAt: string
    isPurchased: boolean
}

// Mock data holder or import from somewhere
const mockLeads: Lead[] = []

export const leadService = {
    getLeads: async (filter: 'All' | 'Purchased' | 'New' = 'All', page = 0, size = 10) => {
        // Backend expects filter query param "filter"
        // And "sort" (default date_desc)
        const query = new URLSearchParams({ filter, page: page.toString(), size: size.toString() })

        // Note: The response is Page<LeadDetailsResponse>, so we might need to map it
        // For now, assuming direct match or close enough
        return apiClient.get(`${API_CONFIG.ENDPOINTS.LEADS.GET_ALL}?${query.toString()}`)
    },

    getLeadById: async (id: string) => {
        return apiClient.get(API_CONFIG.ENDPOINTS.LEADS.GET_BY_ID(id))
    },

    buyLead: async (id: string) => {
        return apiClient.post(API_CONFIG.ENDPOINTS.LEADS.BUY(id))
    },

    skipLead: async (id: string) => {
        return apiClient.post(API_CONFIG.ENDPOINTS.LEADS.SKIP(id))
    },

    updateStatus: async (id: string, status: string) => {
        const query = new URLSearchParams({ status })
        return apiClient.put(`${API_CONFIG.ENDPOINTS.LEADS.UPDATE_STATUS(id)}?${query.toString()}`)
    }
}
