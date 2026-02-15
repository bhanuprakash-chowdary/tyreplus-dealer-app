import { apiClient, fetchWithMockFallback } from "../api-client"

// ... imports

// ... imports
import { API_CONFIG } from "../api-config"
import { mockOrders, mockRequests } from "../mock-data"
import { Order } from "@/types/order"

export interface Request {
    id: string
    tyreName: string
    vehicle: string // Mapped from vehicleId/details
    quotesReceived: number // Mocked/Derived
    bestQuote: number // Mocked/Derived
    requestedAt: string
    status: string
}

export const orderService = {
    getOrders: async () => {
        const response = await fetchWithMockFallback<any[]>(API_CONFIG.ENDPOINTS.ORDERS.GET_ALL, {}, mockOrders)
        // Map backend order to frontend Order interface
        const mappedOrders: Order[] = response.data.map(o => ({
            ...o,
            // Ensure derived fields are populated if missing
            date: o.orderDate ? new Date(o.orderDate).toLocaleDateString() : o.date,
            total: o.totalAmount || o.total,
            items: (o.items || []).map((i: any) => ({
                ...i,
                tyreName: i.tyreName || i.name,
                unitPrice: i.unitPrice || i.price
            }))
        }))
        return { ...response, data: mappedOrders }
    },

    getRequests: async () => {
        const response = await fetchWithMockFallback<any[]>(API_CONFIG.ENDPOINTS.REQUESTS.GET_ALL, {}, mockRequests)
        // Map backend QuoteRequest to frontend Request interface
        const mappedRequests: Request[] = response.data.map(r => ({
            id: r.id,
            tyreName: r.details || "Tyre Request", // Use details or placeholder
            vehicle: "Unknown Vehicle", // Backend has vehicleId, need fetch or details
            quotesReceived: 0, // Backend missing this
            bestQuote: 0, // Backend missing this
            requestedAt: r.requestDate ? new Date(r.requestDate).toLocaleDateString() : r.requestedAt,
            status: r.status
        }))
        return { ...response, data: mappedRequests }
    },

    getOrderById: async (id: string) => {
        const mockOrder = mockOrders.find((o) => o.id === id)
        const response = await fetchWithMockFallback<any>(
            API_CONFIG.ENDPOINTS.ORDERS.GET_BY_ID(id),
            {},
            mockOrder
        )

        if (response.data) {
            const o = response.data
            const mappedOrder: Order = {
                ...o,
                date: o.orderDate ? new Date(o.orderDate).toLocaleDateString() : o.date,
                total: o.totalAmount || o.total,
                items: (o.items || []).map((i: any) => ({
                    ...i,
                    tyreName: i.tyreName || i.name,
                    unitPrice: i.unitPrice || i.price
                }))
            }
            return { ...response, data: mappedOrder }
        }
        return response
    },
    createRequest: async (request: Partial<Request>) => {
        // Backend expects QuoteRequest DTO
        // Map frontend Request to backend QuoteRequest
        const backendRequest = {
            details: request.tyreName, // Mapping tyreName to details
            vehicleId: request.vehicle, // Assuming vehicle is ID, if not need lookup
            // other fields
        }
        return apiClient.post(API_CONFIG.ENDPOINTS.REQUESTS.CREATE, backendRequest)
    },

    placeOrder: async (orderData: Partial<Order>) => {
        // Map frontend order to backend Order entity
        // Note: Backend expects Order entity structure
        // We need to ensure the payload matches com.tyreplus.dealer.domain.entity.Order
        const backendOrder = {
            // Mapping logic here
            totalAmount: orderData.total,
            items: orderData.items?.map(item => ({
                tyreName: item.tyreName,
                quantity: item.quantity,
                unitPrice: item.unitPrice,
                totalPrice: (item.unitPrice || 0) * item.quantity
            })),
            shippingAddress: orderData.shippingAddress
        }
        return apiClient.post(API_CONFIG.ENDPOINTS.ORDERS.CREATE || "/orders", backendOrder)
    }
}
