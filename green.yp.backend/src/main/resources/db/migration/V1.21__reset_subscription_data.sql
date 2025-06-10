TRUNCATE subscription_feature;
DELETE from subscription where subscription_type != 'DATA_IMPORT_NO_DISPLAY';

INSERT INTO greenyp.subscription (id, version, create_date, last_update_date, line_of_business_id, subscription_type, display_name, sort_order, coming_soon_indicator, start_date, end_date, monthly_autopay_amount, quarterly_autopay_amount, annual_bill_amount, short_description, html_description)
VALUES (0x6ECE5C9DAE1B483FB0AE61B4B3D63FED, 0, '2025-05-29 21:32:40', '2025-05-29 21:32:40',
        null, 'TOP_LEVEL', 'Basic Listing', 0, 'N',
        '2025-04-30', '9999-12-31', 5.00, 15.00, 60.00,
        'Perfect for small local landscaping or gardening businesses', '');

INSERT INTO greenyp.subscription (id, version, create_date, last_update_date, line_of_business_id, subscription_type, display_name, sort_order, coming_soon_indicator, start_date, end_date, monthly_autopay_amount, quarterly_autopay_amount, annual_bill_amount, short_description, html_description)
VALUES (0x900E7344047046AB82E0B85AFE11CD81, 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47',
        null, 'TOP_LEVEL', 'Featured Business Listing', 1, 'N',
        '2025-04-30', '9999-12-31', 29.00, 85.00, 300.00,
        'Ideal for established landscaping and garden businesses', '');

INSERT INTO greenyp.subscription (id, version, create_date, last_update_date, line_of_business_id, subscription_type, display_name, sort_order, coming_soon_indicator, start_date, end_date, monthly_autopay_amount, quarterly_autopay_amount, annual_bill_amount, short_description, html_description)
VALUES (0xA62F4251E1C14F8DA9464EBF9720F686, 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02',
        null, 'TOP_LEVEL', 'Premium Business Listing', 2, 'Y',
        '2025-04-30', '9999-12-31', 0.00, 0.00, 0.00,
        'Enhanced business listing with the most features and functionality', '');

INSERT INTO greenyp.subscription (id, version, create_date, last_update_date, line_of_business_id, subscription_type, display_name, sort_order, coming_soon_indicator, start_date, end_date, monthly_autopay_amount, quarterly_autopay_amount, annual_bill_amount, short_description, html_description)
VALUES (0xCA68C861B985421295ADBF00B22FF50C, 2, '2024-05-20 22:48:02', '2024-05-20 22:48:02',
        null, 'TOP_LEVEL', 'Basic Subscription', 0, 'N',
        '2023-08-31', '2025-05-01', 30.00, 100.00, 300.00,
        'Adds information request form', 'Basic Subscription');

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0x05B1F09CB8DD4AB488B9FC706962D629, 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31', 'Top search placement');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0x0AAD6E147E0844A5B0D2D14A737665B9, 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31', 'Featured in \'Recommended\' section');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0x0D41D6B5A4D04BE1B7350F808DA59881, 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31', 'Unlimited number of locations');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0x15BE9CD3421546FEBB90A4D085D97A4B, 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', 0x900E7344047046AB82E0B85AFE11CD81, '2025-05-29', '9999-12-31', 'Enhanced business profile');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0x19E7E18A5FD0427DA06C8E18A93AAFCE, 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31', 'Eco-certification badges');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0x2C2881F46DBF460D8A8DFF63B0D6ED2C, 0, '2025-05-29 21:32:40', '2025-05-29 21:32:40', 0x6ECE5C9DAE1B483FB0AE61B4B3D63FED, '2025-05-29', '9999-12-31', 'Basic business listing');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0x4641EE5038BB4F6C9DC0333180D9A960, 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', 0x900E7344047046AB82E0B85AFE11CD81, '2025-05-29', '9999-12-31', 'Multiple Location Listings (up to 5)');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0x6CBDBC051AF340F0967B78F147684664, 0, '2025-05-29 21:32:40', '2025-05-29 21:32:40', 0x6ECE5C9DAE1B483FB0AE61B4B3D63FED, '2025-05-29', '9999-12-31', 'Business Hours');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0x74009CC343BE4130A7A7A194F9539D35, 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', 0x900E7344047046AB82E0B85AFE11CD81, '2025-05-29', '9999-12-31', 'Priority placement in searches');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0x98F80400ADB341949FA46F8A01A43D85, 0, '2025-05-29 21:32:40', '2025-05-29 21:32:40', 0x6ECE5C9DAE1B483FB0AE61B4B3D63FED, '2025-05-29', '9999-12-31', 'Single category listing');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0xA30CF49A9A254B688997CD8E665F956D, 0, '2025-05-29 21:32:40', '2025-05-29 21:32:40', 0x6ECE5C9DAE1B483FB0AE61B4B3D63FED, '2025-05-29', '9999-12-31', 'Map Location');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0xCCBF82D2E4D14B15835CEDA3574CC3AF, 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31', 'Extended photo gallery (50+ images)');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0xD71842475EA147308D219AD222C2018C, 0, '2025-05-29 21:32:40', '2025-05-29 21:32:40', 0x6ECE5C9DAE1B483FB0AE61B4B3D63FED, '2025-05-29', '9999-12-31', 'Contact Information');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0xE63B3E6B312E465C8D2A0DA1B9B74C57, 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31', 'Seasonal promotional features');
INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date, feature_name)
VALUES (0xE9D6859BE57242879EDBDEE617ADFFAB, 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', 0x900E7344047046AB82E0B85AFE11CD81, '2025-05-29', '9999-12-31', 'Photo gallery (up to 15 images)');
