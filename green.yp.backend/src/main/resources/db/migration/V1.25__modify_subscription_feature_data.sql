TRUNCATE subscription_feature;

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0x05B1F09CB8DD4AB488B9FC706962D629, 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31',
        'topsearch','Top search placement','Y', null);

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0x0AAD6E147E0844A5B0D2D14A737665B9, 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31',
        'recomended','Featured in \'Recommended\' section' , 'Y', null);

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0x0D41D6B5A4D04BE1B7350F808DA59881, 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31',
        'locations','Unlimited number of locations','Y', '{"maxCount":9999}');

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (uuid_to_bin('f5045614-df48-461e-b674-65a424ec1236'), 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31',
        'products','List up to 50 products','Y', '{"maxCount":50}');

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (uuid_to_bin('36b0a057-8a79-44ec-8ebf-784d7d35ddbf'), 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31',
        'services','List up to 50 services','Y', '{"maxCount":50}');

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0x15BE9CD3421546FEBB90A4D085D97A4B, 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', 0x900E7344047046AB82E0B85AFE11CD81, '2025-05-29', '9999-12-31',
        'enhanced','Enhanced business profile', 'Y', null);

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0x19E7E18A5FD0427DA06C8E18A93AAFCE, 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31',
        'cert-badge','Eco-certification badges','Y', null);

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0x2C2881F46DBF460D8A8DFF63B0D6ED2C, 0, '2025-05-29 21:32:40', '2025-05-29 21:32:40', 0x6ECE5C9DAE1B483FB0AE61B4B3D63FED, '2025-05-29', '9999-12-31',
        'basic','Basic business listing', 'Y', null);

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0x4641EE5038BB4F6C9DC0333180D9A960, 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', 0x900E7344047046AB82E0B85AFE11CD81, '2025-05-29', '9999-12-31',
        'locations','Multiple Location Listings (up to 5)', 'Y', '{"maxCount":5}');

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0x6CBDBC051AF340F0967B78F147684664, 0, '2025-05-29 21:32:40', '2025-05-29 21:32:40', 0x6ECE5C9DAE1B483FB0AE61B4B3D63FED, '2025-05-29', '9999-12-31',
        'hours','Business Hours', 'Y', null);

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0x74009CC343BE4130A7A7A194F9539D35, 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', 0x900E7344047046AB82E0B85AFE11CD81, '2025-05-29', '9999-12-31',
        'priority','Priority placement in searches', 'Y', null);

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0x98F80400ADB341949FA46F8A01A43D85, 0, '2025-05-29 21:32:40', '2025-05-29 21:32:40', 0x6ECE5C9DAE1B483FB0AE61B4B3D63FED, '2025-05-29', '9999-12-31',
        'category','Single category listing', 'Y', '{"maxCount":1}');

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0xA30CF49A9A254B688997CD8E665F956D, 0, '2025-05-29 21:32:40', '2025-05-29 21:32:40', 0x6ECE5C9DAE1B483FB0AE61B4B3D63FED, '2025-05-29', '9999-12-31',
        'map','Map Location', 'Y', null);

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0xCCBF82D2E4D14B15835CEDA3574CC3AF, 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31',
        'gallery','Extended photo gallery (50+ images)','Y', '{"maxGalleryCount":999}');

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0xD71842475EA147308D219AD222C2018C, 0, '2025-05-29 21:32:40', '2025-05-29 21:32:40', 0x6ECE5C9DAE1B483FB0AE61B4B3D63FED, '2025-05-29', '9999-12-31',
        'contact','Contact Information', 'Y', null);

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0xE63B3E6B312E465C8D2A0DA1B9B74C57, 0, '2025-05-29 21:38:02', '2025-05-29 21:38:02', 0xA62F4251E1C14F8DA9464EBF9720F686, '2025-05-29', '9999-12-31',
        'seasonal-promo','Seasonal promotional features', 'Y', null);

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (0xE9D6859BE57242879EDBDEE617ADFFAB, 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', 0x900E7344047046AB82E0B85AFE11CD81, '2025-05-29', '9999-12-31',
        'gallery', 'Photo gallery (up to 15 images)', 'Y', '{"maxGalleryCount":15}');

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (uuid_to_bin('c3683a7f-3c6b-4ea3-9cec-6c99f839a007'), 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', 0x900E7344047046AB82E0B85AFE11CD81, '2025-05-29', '9999-12-31',
        'logo', 'Display a business logo', 'Y', '{"logo":1}');

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (uuid_to_bin('a5e77ba4-7ea2-4ff7-b888-566252722df3'), 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', uuid_to_bin('a62f4251-e1c1-4f8d-a946-4ebf9720f686'), '2025-05-29', '9999-12-31',
        'logo', 'Display a business logo', 'N', '{"logo":1}');

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (uuid_to_bin('f78c6ce2-889b-4b16-8720-f96225432e1a'), 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', 0x900E7344047046AB82E0B85AFE11CD81, '2025-05-29', '9999-12-31',
        'products', 'List up to 10 products', 'Y', '{"maxCount":10}');

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (uuid_to_bin('1482f6ea-a279-4e28-ba3b-44411185a955'), 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', 0x900E7344047046AB82E0B85AFE11CD81, '2025-05-29', '9999-12-31',
        'services', 'List up to 10 services', 'Y', '{"maxCount":10}');

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (uuid_to_bin('50f794e5-fc29-476b-900f-d9653f49e79b'), 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', 0x900E7344047046AB82E0B85AFE11CD81, '2025-05-29', '9999-12-31',
        'category', 'Listing in up to 3 categories', 'Y', '{"maxCount":3}');

INSERT INTO greenyp.subscription_feature (id, version, create_date, last_update_date, subscription_id, start_date, end_date,
                                          feature, feature_name, display, config)
VALUES (uuid_to_bin('13ced561-30d4-494f-833f-4569fc30edc2'), 0, '2025-05-29 21:35:47', '2025-05-29 21:35:47', uuid_to_bin('a62f4251-e1c1-4f8d-a946-4ebf9720f686'), '2025-05-29', '9999-12-31',
        'category', 'Listing in up to 6 categories', 'Y', '{"maxCount":6}');

UPDATE subscription_feature set sort_order=0 where feature='basic' or feature='enhanced';
UPDATE subscription_feature set sort_order=1 where feature in ('category');
UPDATE subscription_feature set sort_order=2 where feature = 'locations';
UPDATE subscription_feature set sort_order=3 where feature='logo';
UPDATE subscription_feature set sort_order=4 where feature='gallery';
update subscription_feature set sort_order=5 where feature='products';
update subscription_feature set sort_order=6 where feature='services';
