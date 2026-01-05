-- Create dealers table
CREATE TABLE dealers (
    id UUID PRIMARY KEY,
    business_name VARCHAR(255) NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    password_hash VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(50) NOT NULL UNIQUE,
    alternate_phone_number VARCHAR(50),
    street VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(100),
    zip_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dealer_open_days (
    dealer_id UUID NOT NULL,
    open_day VARCHAR(10) NOT NULL,
    PRIMARY KEY (dealer_id, open_day),
    CONSTRAINT fk_dealer_open_days FOREIGN KEY (dealer_id) REFERENCES dealers(id) ON DELETE CASCADE
);


-- Create leads table
CREATE TABLE leads (
    id UUID PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(50) NOT NULL,
    customer_email VARCHAR(255),
    vehicle_model VARCHAR(255) NOT NULL,
    vehicle_year VARCHAR(10),
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    lead_cost INTEGER NOT NULL,
    purchased_by_dealer_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    purchased_at TIMESTAMP,
    CONSTRAINT fk_lead_dealer FOREIGN KEY (purchased_by_dealer_id) REFERENCES dealers(id)
);

CREATE TABLE lead_skips (
    lead_id UUID NOT NULL,
    dealer_id UUID NOT NULL,
    PRIMARY KEY (lead_id, dealer_id),
    CONSTRAINT fk_lead_skips_lead FOREIGN KEY (lead_id) REFERENCES leads (id)
);

-- Create wallets table
CREATE TABLE wallets (
    id UUID PRIMARY KEY,
    dealer_id UUID NOT NULL UNIQUE,
    credits INTEGER NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallet_dealer FOREIGN KEY (dealer_id) REFERENCES dealers(id)
);

-- Create transactions table
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL,
    dealer_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL,
    credits INTEGER NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id),
    CONSTRAINT fk_transaction_dealer FOREIGN KEY (dealer_id) REFERENCES dealers(id)
);

-- Create OTPs table
CREATE TABLE otps (
    id UUID PRIMARY KEY,
    mobile VARCHAR(20) NOT NULL,
    code VARCHAR(6) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create Package table
CREATE TABLE recharge_packages (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price_in_inr INT NOT NULL CHECK (price_in_inr > 0),
    credits INT NOT NULL CHECK (credits > 0),
    popular BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create indexes
CREATE INDEX idx_leads_status ON leads(status);
CREATE INDEX idx_leads_created_at ON leads(created_at);
CREATE INDEX idx_leads_purchased_by_dealer_id ON leads(purchased_by_dealer_id);
CREATE INDEX idx_wallets_dealer_id ON wallets(dealer_id);
CREATE INDEX idx_transactions_dealer_id ON transactions(dealer_id);
CREATE INDEX idx_transactions_wallet_id ON transactions(wallet_id);
CREATE INDEX idx_transactions_timestamp ON transactions(created_at DESC);
CREATE INDEX idx_otp_mobile_code ON otps(mobile, code);
CREATE INDEX idx_otp_expires_at ON otps(expires_at);
CREATE INDEX idx_recharge_packages_active ON recharge_packages(active);
CREATE INDEX idx_recharge_packages_price ON recharge_packages(price_in_inr);