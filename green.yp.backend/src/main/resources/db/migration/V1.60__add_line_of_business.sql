DELETE FROM lob_service
       where line_of_business_id IN (uuid_to_bin('9c7c7da6-a171-479f-8e85-c669a713b31d'),
                                     uuid_to_bin('de556b8e-4a22-45b5-98c8-b7d9a7aabc9c'),
                                     uuid_to_bin('3d81418b-c435-47dd-8272-3c8bcd49d1d3'),
                                     uuid_to_bin('523a4399-223f-4fb1-a49d-4e0f66dedae1'),
                                     uuid_to_bin('70731dda-d9d0-4e58-8de3-6499ad21985b'));

DELETE from line_of_business where id IN (uuid_to_bin('9c7c7da6-a171-479f-8e85-c669a713b31d'),
                                     uuid_to_bin('de556b8e-4a22-45b5-98c8-b7d9a7aabc9c'),
                                     uuid_to_bin('3d81418b-c435-47dd-8272-3c8bcd49d1d3'),
                                     uuid_to_bin('523a4399-223f-4fb1-a49d-4e0f66dedae1'),
                                     uuid_to_bin('70731dda-d9d0-4e58-8de3-6499ad21985b'));

INSERT INTO greenyp.line_of_business
(id, version, create_date, last_update_date, line_of_business, url_lob,
 icon_name, icon_file_name, created_by_type, create_by_reference,
 enable_distance_radius, short_description, description)
VALUES (uuid_to_bin('9c7c7da6-a171-479f-8e85-c669a713b31d'), 0, current_timestamp, current_timestamp, 'Irrigation', 'irrigation',
        'Waves', null, 'SYSTEM_DEFAULT', 'SYSTEM_DEFAULT',
        'N', 'Installation and repair of irrigation systems',
        'Irrigation professionals specialize in designing, installing, and maintaining systems that keep lawns, gardens, and landscapes healthy and efficiently watered. Whether it’s a residential yard or a commercial property, the right irrigation setup ensures everything stays green without wasting water or money. These experts can help with everything from new system installations to seasonal maintenance and repairs. They also provide water-saving upgrades and smart technology solutions that adapt to weather and plant needs.');

INSERT INTO greenyp.lob_service
    (id, version, create_date, last_update_date,
     line_of_business_id, created_by_type, created_by_reference,
     service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('9c7c7da6-a171-479f-8e85-c669a713b31d'), 'ADMIN_USER', 'admin',
        'Installation', 'Design and installation of new irrigation and sprinkler systems');

INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('9c7c7da6-a171-479f-8e85-c669a713b31d'), 'ADMIN_USER', 'admin',
        'Repair', 'Repair and maintenance of existing irrigation and sprinkler systems');

INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('9c7c7da6-a171-479f-8e85-c669a713b31d'), 'ADMIN_USER', 'admin',
        'Testing and Audits', 'Backflow testing and compliance, water efficiency audits and system upgrades');


INSERT INTO greenyp.line_of_business
(id, version, create_date, last_update_date, line_of_business, url_lob,
 icon_name, icon_file_name, created_by_type, create_by_reference,
 enable_distance_radius, short_description, description)
VALUES (uuid_to_bin('de556b8e-4a22-45b5-98c8-b7d9a7aabc9c'), 0, current_timestamp, current_timestamp, 'Tree Services', 'tree-service',
        'Axe', null, 'SYSTEM_DEFAULT', 'SYSTEM_DEFAULT',
        'N', 'Trimming, cutting, and removal of trees',
        'Keep your outdoor spaces safe, beautiful, and healthy with professional tree services. Whether you''re dealing with overgrown branches, storm-damaged limbs, or planning routine maintenance, skilled arborists can help preserve the health of your trees while enhancing curb appeal. Tree service professionals use the right tools and techniques to handle jobs of all sizes—safely and efficiently.');

INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('de556b8e-4a22-45b5-98c8-b7d9a7aabc9c'), 'ADMIN_USER', 'admin',
        'Tree removal', 'Cutting down (removal) of existing trees');

INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('de556b8e-4a22-45b5-98c8-b7d9a7aabc9c'), 'ADMIN_USER', 'admin',
        'Pruning', 'Cutting low hanging branches and dead branches');

INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('de556b8e-4a22-45b5-98c8-b7d9a7aabc9c'), 'ADMIN_USER', 'admin',
        'Planting', 'Planting and transplanting of trees in the landscape');

INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES ( uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('de556b8e-4a22-45b5-98c8-b7d9a7aabc9c'), 'ADMIN_USER', 'admin',
        'Health Checkup', 'Tree health assessments and care, including disease or pest treatment');

INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('de556b8e-4a22-45b5-98c8-b7d9a7aabc9c'), 'ADMIN_USER', 'admin',
        'Emergency Services', 'Emergency tree services for storm cleanup and urgent removals');

INSERT INTO greenyp.line_of_business
(id, version, create_date, last_update_date, line_of_business, url_lob,
 icon_name, icon_file_name, created_by_type, create_by_reference,
 enable_distance_radius, short_description, description)
VALUES (uuid_to_bin('3d81418b-c435-47dd-8272-3c8bcd49d1d3'), 0, current_timestamp, current_timestamp, 'Cleanup', 'cleanup',
        'Trash', null, 'SYSTEM_DEFAULT', 'SYSTEM_DEFAULT',
        'N', 'Yard and property cleanup and debris removal','Restore order to your outdoor spaces by removing unwanted debris, overgrowth, and waste from your yard, garden, or property. Whether you''re tackling a seasonal cleanup, clearing storm damage, or preparing your home for sale, these services save you time and effort while improving curb appeal and safety. Cleanup professionals handle the mess so you don’t have to — perfect for homeowners, property managers, and real estate agents looking to maintain a clean, healthy landscape.');
INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('3d81418b-c435-47dd-8272-3c8bcd49d1d3'), 'ADMIN_USER', 'admin',
        'Yard Cleanup', 'Removal of fallen leaves, branches, brush, weeds, etc');

INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('3d81418b-c435-47dd-8272-3c8bcd49d1d3'), 'ADMIN_USER', 'admin',
        'Storm Debris Removal', 'Removal of fallen trees, limbs and other debris after storms');

INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('3d81418b-c435-47dd-8272-3c8bcd49d1d3'), 'ADMIN_USER', 'admin',
        'Haul-away Services', 'Hauling away and disposal of junk, bulk waste, etc. ');


INSERT INTO greenyp.line_of_business
(id, version, create_date, last_update_date, line_of_business, url_lob,
 icon_name, icon_file_name, created_by_type, create_by_reference,
 enable_distance_radius, short_description, description)
VALUES (uuid_to_bin('523a4399-223f-4fb1-a49d-4e0f66dedae1'), 0, current_timestamp, current_timestamp, 'Hauling', 'hauling',
        'Truck', null, 'SYSTEM_DEFAULT', 'SYSTEM_DEFAULT',
        'N', 'Transport of dirt, gravel, and other bulk materials',
        'Transport for heavy or bulk materials like dirt, gravel, sand, mulch, debris, and construction waste. Whether you''re starting a landscaping project, managing a construction site, or just need a large load delivered or removed, professional haulers ensure the job gets done efficiently and safely. Ideal for homeowners, contractors, landscapers, and property managers, hauling providers use specialized trucks and equipment to move materials to or from your location — saving you time, labor, and multiple trips.');

INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('523a4399-223f-4fb1-a49d-4e0f66dedae1'), 'ADMIN_USER', 'admin',
        'Bulk Materials', 'Delivery of bulk landscape materials (gravel, soil, mulch, stone, etc.)');

INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('523a4399-223f-4fb1-a49d-4e0f66dedae1'), 'ADMIN_USER', 'admin',
        'Material removal', 'Removal of dirt or debris from excavation or grading projects, Hauling away construction waste or demolition materials');

INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('523a4399-223f-4fb1-a49d-4e0f66dedae1'), 'ADMIN_USER', 'admin',
        'Single or Multiple', 'Single loads or ongoing project support');

INSERT INTO greenyp.line_of_business
(id, version, create_date, last_update_date, line_of_business, url_lob,
 icon_name, icon_file_name, created_by_type, create_by_reference,
 enable_distance_radius, short_description, description)
VALUES (uuid_to_bin('70731dda-d9d0-4e58-8de3-6499ad21985b'), 0, current_timestamp, current_timestamp, 'Forestry Mulching', 'forest-mulch',
        'Truck', null, 'SYSTEM_DEFAULT', 'SYSTEM_DEFAULT',
        'N', 'Clearing small trees and underbrush',
        'Fast, eco-friendly method of land clearing that uses specialized equipment to grind trees, brush, and vegetation into mulch right on site. Unlike traditional methods that require hauling debris away, forestry mulching leaves a layer of organic mulch behind—helping to prevent erosion and promote healthy soil. Forestry mulching is efficient, low-impact, and often more affordable than traditional land clearing services—making it a great option for residential, commercial, and agricultural properties.');

INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('70731dda-d9d0-4e58-8de3-6499ad21985b'), 'ADMIN_USER', 'admin',
        'Land Clearing', 'Clearing overgrown land for new construction or property maintenance');
INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('70731dda-d9d0-4e58-8de3-6499ad21985b'), 'ADMIN_USER', 'admin',
        'Create Firebreaks', 'Reduce risk of extensive property damage due to wildfire');
INSERT INTO greenyp.lob_service
(id, version, create_date, last_update_date,
 line_of_business_id, created_by_type, created_by_reference,
 service_name, service_description)
VALUES (uuid_to_bin(uuid()), 0, current_timestamp, current_timestamp,
        uuid_to_bin('70731dda-d9d0-4e58-8de3-6499ad21985b'), 'ADMIN_USER', 'admin',
        'Land Management', 'Low impact, eco friendly removal of overgrowth and management of invasives');