DROP database ITBMS;
CREATE DATABASE IF NOT EXISTS ITBMS;
USE ITBMS;

CREATE TABLE brand (
    brandId INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) CHARACTER SET utf8mb4 NOT NULL,
    websiteUrl VARCHAR(40) CHARACTER SET utf8mb4,
    isActive BOOLEAN NOT NULL DEFAULT TRUE,
    countryOfOrigin VARCHAR(80) CHARACTER SET utf8mb4,
    createdOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP  ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE sale_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    brandId INT NOT NULL,
    model VARCHAR(60) CHARACTER SET utf8mb4 NOT NULL,
    description VARCHAR(500) CHARACTER SET utf8mb4 NOT NULL,
    price INT NOT NULL,
    ramGb INT,
    screenSizeInch DECIMAL(3,1),
    storageGb INT,
    color VARCHAR(50) CHARACTER SET utf8mb4,
    quantity INT NOT NULL DEFAULT 1,
    createdOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_brand FOREIGN KEY (brandId) REFERENCES brand(brandId)
);

INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive ,createdOn, updatedOn) VALUES
('Samsung', 'South Korea', 'https://www.samsung.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('Apple', 'United States', 'https://www.apple.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('Xiaomi', 'China', 'https://www.mi.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('Huawei', 'China', 'https://www.huawei.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('OnePlus', 'China', 'https://www.oneplus.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('Sony', 'Japan', 'https://www.sony.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('LG', 'South Korea', 'https://www.lg.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('Nokia', 'Finland', 'https://www.nokia.com', 0, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('Motorola', 'United States', 'https://www.motorola.com', 0, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('OPPO', 'China', 'https://www.oppo.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('Vivo', 'China', 'https://www.vivo.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('ASUS', 'Taiwan', 'https://www.asus.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('Google', 'United States', 'https://store.google.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('Realme', 'China', 'https://www.realme.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('BlackBerry', 'Canada', 'https://www.blackberry.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('HTC', 'Taiwan', 'https://www.htc.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('ZTE', 'China', 'https://www.zte.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('Lenovo', 'China', 'https://www.lenovo.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('Honor', 'China', 'https://www.hihonor.com', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00'),
('Nothing', 'United Kingdom', 'https://nothing.tech', 1, '2024-04-19 01:00:00', '2024-04-19 01:00:00');

INSERT INTO sale_item 
(brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color) 
VALUES
(2, 'iPhone 14 Pro Max', 'ไอโฟนเรือธงรุ่นล่าสุด มาพร้อม Dynamic Island จอใหญ่สุดในตระกูล กล้องระดับโปร', 5, 42900, 6.7, 6, 512, 'Space Black'),
(2, 'iPhone 14', 'ไอโฟนรุ่นใหม่ล่าสุด รองรับ 5G เร็วแรง ถ่ายภาพสวยทุกสภาพแสง', 8, 29700, 6.1, 6, 256, 'Midnight'),
(2, 'iPhone 13 Pro', 'ไอโฟนรุ่นโปร จอ ProMotion 120Hz กล้องระดับมืออาชีพ', 3, 33000, 6.1, 6, 256,NULL),
(2, 'iPhone SE 2022', 'Budget-friendly model', 15, 14190, 4.7, 4, 64, 'Starlight'),
(2, 'iPhone 14 Plus', 'iPhone 14 Plus 128GB สี Starlight เครื่องศูนย์ไทย โมเดล TH แบต 100% มีกล่องครบ ประกันศูนย์ถึง พ.ย. 68 ส่งฟรี', 7, 29700, 6.7, 6, 256, 'Blue'),
(1, 'Galaxy S23 Ultra', 'Samsung Galaxy S23 Ultra 512GB สดี าปีศาจ สภาพนางฟ้า 99% ไร้รอย แถมเคสแท้ แบตอึดสุดๆ รองรับปากกา S-Pen', 1, 32900, 7.6, NULL, 512, NULL),
(3, 'Xiaomi 12T', 'เรือธงสุดคุ้ม กล้อง 108MP จอ AMOLED 120Hz', 10, 17990, 6.67, 8, 256, 'Black'),
(3, 'Redmi Note 11', 'จอ AMOLED กล้องชัด แบตอึด รองรับชาร์จไว 33W', 20, 6490, 6.43, 4, 128, 'Graphite Gray'),
(4, 'Huawei P50 Pro', 'กล้อง Leica ขั้นเทพ ดีไซน์บางเฉียบ', 4, 24990, 6.6, 8, 256, 'Cocoa Gold'),
(5, 'OnePlus 11', 'OnePlus 11 5G สเปกแรง กล้อง Hasselblad', 6, 28990, 6.7, 12, 256, 'Titan Black');
 