update subscription
set annual_bill_amount = 12 * monthly_autopay_amount,
    quarterly_autopay_amount = 3 * monthly_autopay_amount
where
    end_date = '9999-12-31'
    and subscription_type != 'DATA_IMPORT_NO_DISPLAY'