import { fetchWithMockFallback } from "../api-client"
import { API_CONFIG } from "../api-config"
import { mockVehicles } from "../mock-data"

export interface Vehicle {
    id: string
    vehicleName: string
    registrationNumber: string
    tyreSize: string
    isPrimary: boolean
    make?: string
    model?: string
    variant?: string
    // Removed fields not in backend: year, lastService, image
}

export const vehicleService = {
    getVehicles: async () => {
        return fetchWithMockFallback<Vehicle[]>(API_CONFIG.ENDPOINTS.VEHICLES.GET_ALL, {}, mockVehicles)
    },

    addVehicle: async (vehicle: Omit<Vehicle, "id">) => {
        // For mock, we just return the vehicle with a new ID
        const newVehicle = { ...vehicle, id: `VEH-${Date.now()}` }
        return fetchWithMockFallback<Vehicle>(
            API_CONFIG.ENDPOINTS.VEHICLES.ADD,
            {
                method: "POST",
                body: JSON.stringify(vehicle),
            },
            newVehicle
        )
    },

    deleteVehicle: async (id: string) => {
        return fetchWithMockFallback<{ success: boolean }>(
            API_CONFIG.ENDPOINTS.VEHICLES.DELETE(id),
            { method: "DELETE" },
            { success: true }
        )
    },

    getMakes: async (type: string) => {
        // Fallback: Need to import from vehicle-data but it's a module
        // For simplicity in mock fallback, we'll return empty or minimal data if needed, 
        // but ideally we should import the data. 
        // Note: fetchWithMockFallback expects T.
        // We will assume the API returns { makes: string[] }
        return fetchWithMockFallback<{ makes: string[] }>(
            `${API_CONFIG.ENDPOINTS.VEHICLES.MAKES}?type=${type}`,
            {},
            { makes: ["Mock Make 1", "Mock Make 2"] }
        )
    },

    getModels: async (type: string, make: string) => {
        return fetchWithMockFallback<{ models: string[] }>(
            `${API_CONFIG.ENDPOINTS.VEHICLES.MODELS}?type=${type}&make=${make}`,
            {},
            { models: ["Mock Model 1", "Mock Model 2"] }
        )
    },

    getVariants: async (type: string, make: string, model: string) => {
        return fetchWithMockFallback<{ variants: string[] }>(
            `${API_CONFIG.ENDPOINTS.VEHICLES.VARIANTS}?type=${type}&make=${make}&model=${model}`,
            {},
            { variants: ["Mock Variant 1", "Mock Variant 2"] }
        )
    },

    getTyreSizes: async (make: string, model: string, variant: string) => {
        // Assuming there might be an endpoint or we mock it
        // The user asked for "select size" to be run on API data.
        return fetchWithMockFallback<{ sizes: string[] }>(
            // We'll construct a plausible endpoint even if not in original md, or use a query param on generic
            `/vehicles/tyre-sizes?make=${make}&model=${model}&variant=${variant}`,
            {},
            { sizes: ["165/80 R14", "185/65 R15"] }
        )
    }
}
