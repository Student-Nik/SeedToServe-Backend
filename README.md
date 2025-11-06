# 🌱 SeedToServe – Empowering Farmers Through Direct E-Commerce

**SeedToServe** is an e-commerce platform built to empower farmers in India by allowing them to list their products directly for customers to purchase — eliminating middlemen and ensuring fair prices.  
The platform is powered by **Spring Boot (Backend)** with **JWT Authentication** and integrated **Razorpay Payment Gateway** for secure transactions.

---

## 🚀 Features

### 🧑‍🌾 Farmer Module
- Farmers can register and list their products.
- Add, update, or remove items from inventory.
- View and manage received orders.

### 🛒 Customer Module
- Customers can browse products from farmers.
- Add items to cart and place orders.
- Make secure online payments via Razorpay.

### 💳 Payment Integration (Razorpay)
- Razorpay integrated from scratch using backend + frontend.
- Amount is dynamically calculated from order total and directly sent to Razorpay for payment.
- On successful payment, payment status is updated in DB as `PAID`.
- Transaction details are securely verified using **HMAC SHA256 signature verification**.
- Includes **CORS-enabled** setup for smooth communication between backend and frontend.

### 🔐 JWT Authentication
- Secure login and signup for both customers and farmers.
- JWT tokens used for user verification and session handling.
- Protected APIs for user-specific operations.

### 🧰 Admin Module (Optional)
- Admin can view all registered farmers and customers.
- Manage product approvals and monitor transactions.

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|-------------|
| **Backend** | Spring Boot, Spring Security, JWT, Hibernate, JPA |
| **Database** | MySQL |
| **Payment** | Razorpay Payment Gateway |
| **Frontend** | HTML, CSS, JavaScript (TailwindCSS optional) |
| **Build Tool** | Maven |
| **IDE** | Spring Tool Suite / IntelliJ IDEA |

---

## ⚙️ How It Works (Payment Flow)

1. When the user proceeds to pay, the total amount is calculated in the backend.
2. Backend creates a **Razorpay Order** using Razorpay API.
3. The order amount (in paise) and order ID are sent to the frontend.
4. Razorpay Checkout UI opens and processes the payment.
5. On successful payment:
   - Razorpay returns `razorpay_payment_id`, `razorpay_order_id`, and `razorpay_signature`.
   - Backend verifies the signature using **HMAC SHA256**.
   - If valid, the payment status is marked **PAID** in the database.

---



---

## 🔑 Security Notes

- `application.properties` is **ignored in Git** for safety.
- Keep your secrets in local environment variables:
  ```properties
  razorpay.key.id=your_key_id
  razorpay.key.secret=your_secret_key
  jwt.secret=your_jwt_secret
  spring.datasource.password=your_password


## 🧩 API Highlights

| Endpoint | Method | Description |
|-----------|---------|-------------|
| `/api/auth/register` | POST | Register new user |
| `/api/auth/login` | POST | Login & receive JWT |
| `/api/payment/create-payment/{orderId}` | POST | Create Razorpay order |
| `/api/payment/verify` | POST | Verify Razorpay signature |
| `/api/products` | GET | Get all products |
| `/api/orders` | GET | Get user’s orders |

---

## ⚡ Running the Application

### 🧱 Prerequisites
- JDK 17+
- Maven
- MySQL running locally
- Razorpay Account (for testing)

### 🧩 Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/SeedToServe.git
   cd SeedToServe

2. **Configure application.properties (or use .env variables)**
   ```bash
   spring.datasource.url=jdbc:mysql://localhost:3306/seedtoserve
   spring.datasource.username=root
   spring.datasource.password=your_password

   razorpay.key.id=your_key_id
   razorpay.key.secret=your_secret_key

3. **Run the app**
   ```bash
   mvn spring-boot:run

4. **Open frontend (Razorpay checkout page or web UI)**

   Test the complete order-to-payment flow end-to-end.


### 🧠 Key Learnings

1. Implemented JWT Authentication for secure user access.

Integrated Razorpay Payment Gateway (backend + frontend).

2. Added CORS configuration for smooth API communication.

3. Followed Spring Boot best practices and layered architecture (Controller → Service → Repository).

4. Implemented HMAC SHA256 signature verification for secure payment confirmation.


### 🤝 Author

👨‍💻 Nikhil Kute
Student Developer | Passionate about Spring Boot, DevOps, and Cloud
📧 nkute611@gmail.com
🌐 www.linkedin.com/in/nikhil-kute-java

### Show Support

If you like this project, please give it a ⭐ on GitHub!
Your support motivates continued learning and improvements. 🌟
