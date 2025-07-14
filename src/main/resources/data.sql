-- Sample data for Feedback and dependencies

-- Insert Customers
INSERT INTO Customers (FullName, Phone, Email, MedicalHistory, Allergies, TotalPoints, CreatedDate)
VALUES 
('Nguyễn Văn An', '0901234567', 'an.nguyen@email.com', '', '', 120, '2024-01-10'),
('Trần Thị Bình', '0912345678', 'binh.tran@email.com', '', 'Dị ứng penicillin', 80, '2024-01-15'),
('Lê Minh Cường', '0923456789', 'cuong.le@email.com', '', '', 150, '2024-02-01'),
('Phạm Thị Dung', '0934567890', 'dung.pham@email.com', '', '', 60, '2024-02-10'),
('Vũ Thị Hạnh', '0945678901', 'hanh.vu@email.com', '', '', 200, '2024-03-01');

-- Insert Pharmacies
INSERT INTO Pharmacies (PharmacyName, Address, Phone, Email, CreatedDate)
VALUES 
('Long Châu 1', '123 Lê Lợi, Q.1', '0281234567', 'lc1@email.com', '2024-01-01'),
('Long Châu 2', '456 Nguyễn Trãi, Q.5', '0282345678', 'lc2@email.com', '2024-01-01');

-- Insert Users (for handledByUser, minimal)
INSERT INTO Users (Username, Password, FullName, Role, IsActive)
VALUES 
('admin', 'admin', 'Admin', 'admin', 1),
('cs1', 'cs1', 'Nguyễn CS', 'employee', 1);

-- Ensure Notes column exists for reminders
ALTER TABLE Invoices ADD Notes NVARCHAR(MAX) NULL;

-- Insert Invoices (for Feedback foreign key)
INSERT INTO Invoices (PharmacyID, CustomerID, UserID, InvoiceDate, TotalAmount, PointsEarned, Payment, Status, Notes)
VALUES 
(1, 1, 2, '2024-03-01T10:00:00', 500000, 50, N'Tiền mặt', 'paid', N'Uống 1 viên sau ăn sáng và tối'),
(2, 2, 2, '2024-03-02T11:00:00', 300000, 30, N'Chuyển khoản', 'cancle', N'Đơn hàng đã hủy, không cần dùng thuốc'),
(1, 3, 2, '2024-03-03T12:00:00', 700000, 70, N'Tiền mặt', 'paid', N'Dùng trước khi đi ngủ 30 phút'),
(2, 4, 2, '2024-03-04T13:00:00', 200000, 20, N'Tiền mặt', 'cancle', N'Donh hàng hủy, lưu ý kiểm tra lại thông tin'),
(1, 5, 2, '2024-03-05T14:00:00', 1000000, 100, N'Chuyển khoản', 'paid', N'Uống đủ liệu trình theo hướng dẫn bác sĩ');

-- Insert Feedbacks

-- Note: LoyaltyPoints functionality has been removed from customer service system
