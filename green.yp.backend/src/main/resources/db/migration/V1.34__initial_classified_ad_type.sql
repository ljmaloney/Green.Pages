delete from classified_ad_type;

insert into classified_ad_type
  (id, version, create_date, last_update_date, active, sort_order, ad_type_name, monthly_price, three_month_price, features)
values
(uuid_to_bin('7ef5bc35-e8c6-452a-aedd-9b7ac080334f'), 0, current_timestamp, current_timestamp,
 'Y', 0,'Basic', 10.00, 30.00, '{"features":["No Images"], "maxImages":0, "protectContact":false}');

insert into classified_ad_type
(id, version, create_date, last_update_date, active, sort_order,ad_type_name, monthly_price, three_month_price, features)
values
(uuid_to_bin('03eaa115-89ec-4af6-8dee-2e6e516ed279'), 0, current_timestamp, current_timestamp,
 'Y', 1,'Standard', 20.00, 60.00, '{"features":["Up to 5 images", "Contact Privacy"], "maxImages":5, "protectContact":true}');

insert into classified_ad_type
(id, version, create_date, last_update_date, active, sort_order, ad_type_name, monthly_price, three_month_price, features)
values
(uuid_to_bin('01f78a73-6500-44ee-87f9-24119a62eb0a'), 0, current_timestamp, current_timestamp,
 'Y', 2,'Enhanced', 30.00, 90.00, '{"features":["Up to 10 images", "Contact Privacy"], "maxImages":10, "protectContact":true}');
