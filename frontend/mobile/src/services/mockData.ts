// Mock Data for TyrePlus Dealer App

export const MOCK_USER = {
    id: 'dealer_123',
    businessName: 'Super Tyres Ltd.',
    ownerName: 'Rajesh Kumar',
    mobile: '+91 98765 43210',
    email: 'dealer@supertyres.com',
    whatsapp: '9876543210',
    gstNumber: '29ABCDE1234F1Z5',
    yearsInBusiness: '12',
    address: {
        shopNumber: '123',
        street: 'Auto Market, MG Road',
        city: 'Bangalore',
        state: 'Karnataka',
        pincode: '560001',
        landmark: 'Near Central Bank'
    },
    businessHours: {
        openTime: '09:00 AM',
        closeTime: '08:00 PM',
        openDays: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
    },
    services: ['Tyre Sales', 'Wheel Alignment', 'Wheel Balancing', 'Tyre Fitting'],
    brands: ['MRF', 'CEAT', 'Michelin', 'Bridgestone'],
    isVerified: true,
    avatar: 'https://via.placeholder.com/150'
};

export const MOCK_DASHBOARD = {
    walletBalance: 2450,
    stats: {
        newLeads: 12,
        conversionRate: 85
    },
    recentLeads: [
        { id: '1', name: 'Rajesh Kumar', vehicle: 'Hyundai Creta', location: 'Indiranagar, Bangalore', date: 'Today, 10:30 AM', status: 'New' },
        { id: '2', name: 'Amit Singh', vehicle: 'Maruti Swift', location: 'Koramangala, Bangalore', date: 'Today, 09:15 AM', status: 'New' },
        { id: '3', name: 'Priya Sharma', vehicle: 'Honda City', location: 'HSR Layout, Bangalore', date: 'Yesterday', status: 'Follow-up' }
    ]
};

export const MOCK_LEADS_LIST = {
    content: [
        {
            id: '382bd81d-deee-4243-8748-d4aaf8c51642',
            customerName: 'Neha Gupta',
            customerPhone: '9666655555',
            customerEmail: 'neha.gupta@example.com',
            vehicleModel: 'BMW 3 Series',
            vehicleYear: '2019',
            status: 'NEW',
            leadCost: 250,
            purchasedByDealerId: null,
            createdAt: '2026-01-04T19:11:28.945192',
            purchasedAt: null
        },
        {
            id: '2',
            customerName: 'Amit Singh',
            customerPhone: '9876543210',
            customerEmail: 'amit@example.com',
            vehicleModel: 'Maruti Swift',
            vehicleYear: '2020',
            status: 'NEW',
            leadCost: 100,
            purchasedByDealerId: null,
            createdAt: '2026-01-05T09:15:00.000000',
            purchasedAt: null
        },
        {
            id: '3',
            customerName: 'Priya Sharma',
            customerPhone: '9988776655',
            customerEmail: 'priya@example.com',
            vehicleModel: 'Honda City',
            vehicleYear: '2021',
            status: 'FOLLOW_UP',
            leadCost: 150,
            purchasedByDealerId: 'dealer_123',
            createdAt: '2026-01-04T10:00:00.000000',
            purchasedAt: '2026-01-04T10:05:00.000000'
        }
    ],
    page: {
        number: 0,
        size: 10,
        totalElements: 3,
        totalPages: 1
    }
};

export const MOCK_LEAD_DETAILS = {
    id: '1',
    status: 'New',
    customer: {
        name: 'Rajesh Kumar',
        mobile: '+91 9876543210',
        location: 'Indiranagar, Bangalore'
    },
    vehicle: {
        model: 'Hyundai Creta',
        year: '2022'
    },
    serviceRequirement: '4 Tyres Replacement + Alignment',
    leadCost: 50,
    questionnaire: [
        { id: '1', question: 'Urgency', answer: 'Immediately' },
        { id: '2', question: 'Issues with current tyres', answer: ['Low tread / smooth tyre', 'Frequent punctures', 'Skidding or poor grip'] },
        { id: '3', question: 'Vehicle Usage', answer: 'Daily City Use' },
        { id: '4', question: 'Budget', answer: 'Balanced' },
        { id: '5', question: 'Specific Preferences', answer: ['Warranty required', 'Genuine brand only'] },
    ]
};

export const MOCK_WALLET = {
    balance: 2450,
    packages: [
        { id: '1', name: 'Starter', price: '₹ 500', credits: '10 Leads' },
        { id: '2', name: 'Growth', price: '₹ 2,000', credits: '50 Leads', popular: true },
        { id: '3', name: 'Pro', price: '₹ 5,000', credits: '150 Leads' },
    ],
    history: [
        { id: '1', title: 'Added Money', date: 'Today, 10:00 AM', amount: '+ ₹ 2,000', type: 'credit' },
        { id: '2', title: 'Lead Purchase', date: 'Yesterday', amount: '- ₹ 50', type: 'debit' },
        { id: '3', title: 'Lead Purchase', date: '25 Dec', amount: '- ₹ 50', type: 'debit' },
        { id: '4', title: 'Added Money', date: '20 Dec', amount: '+ ₹ 500', type: 'credit' },
    ]
};

export const MOCK_STATS = {
    performanceScore: 8.5,
    percentile: 'Top 5%',
    metrics: {
        totalLeads: 124,
        conversionRate: '42%',
        avgRating: 4.8,
        avgResponseTime: '15m'
    },
    ratingBreakdown: [
        { label: '5 Star', percentage: 80, color: '#16A34A' },
        { label: '4 Star', percentage: 15, color: '#84CC16' },
        { label: '3 Star', percentage: 5, color: '#FACC15' }
    ]
};

export const MOCK_NOTIFICATIONS_SETTINGS = {
    pushEnabled: true,
    emailEnabled: false,
    whatsappEnabled: true,
    soundEnabled: false
};
