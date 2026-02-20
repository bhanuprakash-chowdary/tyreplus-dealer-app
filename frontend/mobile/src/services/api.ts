import { Alert } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {
    MOCK_USER,
    MOCK_DASHBOARD,
    MOCK_LEADS_LIST,
    MOCK_LEAD_DETAILS,
    MOCK_WALLET,
    MOCK_STATS,
    MOCK_NOTIFICATIONS_SETTINGS
} from './mockData';

// Configuration
// Android Emulator : 'http://10.0.2.2:8080'
// iOS Simulator    : 'http://localhost:8080'
// Physical device  : 'http://<your-LAN-IP>:8080'
const API_BASE_URL = process.env.EXPO_PUBLIC_API_URL || 'http://10.0.2.2:8080';
const USE_MOCK_ON_FAIL = true; // Feature flag for fallback
const USE_MOCK_ONLY = true; // New flag to force mock data

// Helper to simulate network delay
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

// Generic Fetch Wrapper
async function apiFetch<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    if (USE_MOCK_ONLY) {
        throw new Error('Mock Mode Enabled');
    }
    const url = `${API_BASE_URL}${endpoint}`;
    console.log(`[API REQUEST] ${options.method || 'GET'} ${url}`);
    if (options.body) {
        console.log('[API BODY]', options.body);
    }

    try {
        const token = await AsyncStorage.getItem('userToken');
        const headers: HeadersInit = {
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
        };

        const response = await fetch(url, {
            headers,
            ...options,
        });

        console.log(`[API RESPONSE] Status: ${response.status}`);

        if (!response.ok) {
            const errorText = await response.text();
            console.error(`[API ERROR] ${response.status} - ${errorText}`);
            throw new Error(`API Error: ${response.status}`);
        }

        const data = await response.json();
        console.log('[API DATA]', data);
        return data as T;
    } catch (error) {
        console.warn(`[API FAILURE] Failed to fetch ${endpoint}:`, error);
        throw error;
    }
}

// --- API Methods ---

// Auth
export const sendOtp = async (mobile: string) => {
    if (USE_MOCK_ONLY) {
        await delay(1000);
        return { success: true, message: 'OTP sent successfully' };
    }
    try {
        return await apiFetch('/api/v1/auth/dealer/quick/send-otp', { method: 'POST', body: JSON.stringify({ mobile }) });
    } catch (error) {
        if (USE_MOCK_ON_FAIL) {
            await delay(1000);
            return { success: true, message: 'OTP sent successfully' };
        }
        throw error;
    }
};

export const verifyOtp = async (mobile: string, otp: string) => {
    if (USE_MOCK_ONLY) {
        await delay(1000);
        await AsyncStorage.setItem('userToken', 'mock_token');
        return { token: 'mock_token', user: MOCK_USER };
    }
    try {
        // Backend: POST /api/v1/auth/quick/verify-otp  body: { mobile, otp }
        const response = await apiFetch<any>('/api/v1/auth/dealer/quick/verify-otp', { method: 'POST', body: JSON.stringify({ mobile, otp }) });
        if (response.token) {
            await AsyncStorage.setItem('userToken', response.token);
        }
        return response;
    } catch (error) {
        if (USE_MOCK_ON_FAIL) {
            await delay(1000);
            await AsyncStorage.setItem('userToken', 'mock_token');
            return { token: 'mock_token', user: MOCK_USER };
        }
        throw error;
    }
};

export const loginWithPassword = async (identifier: string, password: string) => {
    if (USE_MOCK_ONLY) {
        await delay(1000);

        // Mock Validation
        const isValidMobile = identifier === '9876543210' && password === 'password123';
        const isValidEmail = identifier === 'dealer@supertyres.com' && password === 'password123';

        if (isValidMobile || isValidEmail) {
            await AsyncStorage.setItem('userToken', 'mock_token');
            return { token: 'mock_token', user: MOCK_USER };
        } else {
            throw new Error('Invalid credentials');
        }
    }
    try {
        const response = await apiFetch<any>('/api/v1/auth/dealer/login', { method: 'POST', body: JSON.stringify({ identifier, password }) });
        if (response.token) {
            await AsyncStorage.setItem('userToken', response.token);
        }
        return response;
    } catch (error) {
        if (USE_MOCK_ON_FAIL) {
            await delay(1000);
            await AsyncStorage.setItem('userToken', 'mock_token');
            return { token: 'mock_token', user: MOCK_USER };
        }
        throw error;
    }
};

export const registerDealer = async (data: any) => {
    if (USE_MOCK_ONLY) {
        await delay(1000);
        return { success: true, message: 'Registration successful' };
    }
    try {
        // Backend: POST /api/v1/auth/register/complete  body: RegisterRequest
        return await apiFetch('/api/v1/auth/dealer/register/complete', { method: 'POST', body: JSON.stringify(data) });
    } catch (error) {
        if (USE_MOCK_ON_FAIL) {
            await delay(1000);
            return { success: true, message: 'Registration successful' };
        }
        throw error;
    }
};

export const registerRoadsideDealer = async (data: any) => {
    if (USE_MOCK_ONLY) {
        await delay(1000);
        return { success: true, message: 'Registration successful' };
    }
    try {
        // Backend uses the same registration endpoint for all dealer types
        return await apiFetch('/api/v1/auth/dealer/register/complete', { method: 'POST', body: JSON.stringify(data) });
    } catch (error) {
        if (USE_MOCK_ON_FAIL) {
            await delay(1000);
            return { success: true, message: 'Registration successful' };
        }
        throw error;
    }
};

export const logout = async () => {
    await AsyncStorage.removeItem('userToken');
};

// Profile
export const getProfile = async () => {
    if (USE_MOCK_ONLY) return MOCK_USER;
    try {
        return await apiFetch('/api/v1/dealer/profile');
    } catch (error) {
        if (USE_MOCK_ON_FAIL) return MOCK_USER;
        throw error;
    }
};

export const updateProfile = async (data: any) => {
    if (USE_MOCK_ONLY) {
        await delay(1000);
        return { ...MOCK_USER, ...data };
    }
    try {
        return await apiFetch('/api/v1/dealer/profile', { method: 'PUT', body: JSON.stringify(data) });
    } catch (error) {
        if (USE_MOCK_ON_FAIL) {
            await delay(1000);
            return { ...MOCK_USER, ...data };
        }
        throw error;
    }
};

// Dashboard
export const getDashboardData = async () => {
    if (USE_MOCK_ONLY) return MOCK_DASHBOARD;
    try {
        return await apiFetch('/api/v1/dealer/dashboard');
    } catch (error) {
        if (USE_MOCK_ON_FAIL) return MOCK_DASHBOARD;
        throw error;
    }
};

// Leads
export const getLeads = async (filter?: string, sort?: string) => {
    if (USE_MOCK_ONLY) {
        if (filter && filter !== 'All') {
            return {
                ...MOCK_LEADS_LIST,
                content: MOCK_LEADS_LIST.content.filter((l: any) => l.status === filter)
            };
        }
        return MOCK_LEADS_LIST;
    }
    try {
        const query = new URLSearchParams();

        // Filter mapping
        if (filter && filter !== 'All') {
            const statusMap: Record<string, string> = {
                'New': 'NEW',
                'Follow-up': 'BOUGHT',
                'Converted': 'CONVERTED'
            };
            query.append('status', statusMap[filter] || filter);
        }

        // Sort mapping
        const sortMap: Record<string, string> = {
            'Date (Newest)': 'date_desc',
            'Date (Oldest)': 'date_asc',
            'Priority': 'priority'
        };
        const mappedSort = sort ? (sortMap[sort] || 'date_desc') : 'date_desc';
        query.append('sort', mappedSort);

        // Pagination
        query.append('page', '0');
        query.append('size', '10');

        return await apiFetch(`/api/v1/leads?${query.toString()}`);
    } catch (error) {
        if (USE_MOCK_ON_FAIL) {
            // Simple local filtering for mock
            if (filter && filter !== 'All') {
                return {
                    ...MOCK_LEADS_LIST,
                    content: MOCK_LEADS_LIST.content.filter((l: any) => l.status === filter)
                };
            }
            return MOCK_LEADS_LIST;
        }
        throw error;
    }
};

export const getUnlockedLeads = async (page = 0, size = 10) => {
    if (USE_MOCK_ONLY) return MOCK_LEADS_LIST;
    try {
        return await apiFetch(`/api/v1/leads/unlocked?page=${page}&size=${size}`);
    } catch (error) {
        if (USE_MOCK_ON_FAIL) return MOCK_LEADS_LIST;
        throw error;
    }
};

export const getLeadDetails = async (leadId: string) => {
    if (USE_MOCK_ONLY) return { ...MOCK_LEAD_DETAILS, id: leadId };
    try {
        return await apiFetch(`/api/v1/leads/${leadId}`);
    } catch (error) {
        if (USE_MOCK_ON_FAIL) return { ...MOCK_LEAD_DETAILS, id: leadId };
        throw error;
    }
};

export const submitOffer = async (leadId: string, offerDetails: any) => {
    if (USE_MOCK_ONLY) {
        await delay(500);
        return { success: true, message: 'Offer submitted successfully' };
    }
    try {
        return await apiFetch(`/api/v1/leads/${leadId}/offer`, {
            method: 'POST',
            body: JSON.stringify(offerDetails)
        });
    } catch (error) {
        if (USE_MOCK_ON_FAIL) {
            await delay(500);
            return { success: true, message: 'Offer submitted successfully' };
        }
        throw error;
    }
};

export const skipLead = async (leadId: string) => {
    if (USE_MOCK_ONLY) {
        await delay(500);
        return { success: true, message: 'Lead skipped' };
    }
    try {
        return await apiFetch(`/api/v1/leads/${leadId}/status?status=SKIPPED`, { method: 'PUT' });
    } catch (error) {
        if (USE_MOCK_ON_FAIL) {
            await delay(500);
            return { success: true, message: 'Lead skipped' };
        }
        throw error;
    }
};

// Wallet
export const getWalletData = async () => {
    if (USE_MOCK_ONLY) return MOCK_WALLET;
    try {
        return await apiFetch('/api/v1/dealer/wallet');
    } catch (error) {
        if (USE_MOCK_ON_FAIL) return MOCK_WALLET;
        throw error;
    }
};

export const getPackages = async () => {
    if (USE_MOCK_ONLY) return MOCK_WALLET.packages;
    try {
        return await apiFetch('/api/v1/dealer/packages');
    } catch (error) {
        if (USE_MOCK_ON_FAIL) return MOCK_WALLET.packages;
        throw error;
    }
};

export const rechargeWallet = async (packageId: string) => {
    if (USE_MOCK_ONLY) {
        await delay(1000);
        return { success: true, message: 'Recharge successful' };
    }
    try {
        return await apiFetch('/api/v1/dealer/wallet/testRecharge', {
            method: 'POST',
            body: JSON.stringify({ packageId })
        });
    } catch (error) {
        if (USE_MOCK_ON_FAIL) {
            await delay(1000);
            return { success: true, message: 'Recharge successful' };
        }
        throw error;
    }
};

// Stats — backend has no dedicated stats endpoint; use dashboard which contains stats
export const getStatsData = async () => {
    if (USE_MOCK_ONLY) return MOCK_STATS;
    try {
        return await apiFetch('/api/v1/dealer/dashboard');
    } catch (error) {
        if (USE_MOCK_ON_FAIL) return MOCK_STATS;
        throw error;
    }
};

// Settings — notification preferences endpoint not yet implemented in backend; always returns mock
export const getNotificationSettings = async () => {
    return MOCK_NOTIFICATIONS_SETTINGS;
};
