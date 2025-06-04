delete from subscription where id = uuid_to_bin('e0315cb5-a2e2-40e1-abb2-4ce646439730');
insert into subscription
    (id, version, create_date, last_update_date, line_of_business_id, subscription_type,
     display_name, sort_order, start_date, end_date, monthly_autopay_amount,
     quarterly_autopay_amount, annual_bill_amount, short_description, html_description)
values (
        uuid_to_bin('e0315cb5-a2e2-40e1-abb2-4ce646439730'), 0,now(), now(), null, 'DATA_IMPORT_NO_DISPLAY',
        'Data import - not available', 0, '2025-01-01', '9999-12-31', 0,
        0, 0, 'Subscription record for data import', ''
       );
