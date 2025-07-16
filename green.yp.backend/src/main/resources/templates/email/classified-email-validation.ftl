<#-- FreeMarker Email Template: base-email.ftl -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>${title!''}</title>
    <#if preheader??>
        <div style="display: none; font-size: 1px; color: #fefefe; line-height: 1px; font-family: Arial, sans-serif; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden;">
            ${preheader}
        </div>
    </#if>
</head>
<body style="margin: 0; padding: 0; background-color: #f9fafb; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;">
<table role="presentation" style="width: 100%; background-color: #f9fafb; margin: 0; padding: 40px 0;">
    <tr>
        <td align="center">
            <table role="presentation" style="width: 100%; max-width: 800px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1); overflow: hidden;">
                <!-- Header -->
                <tr>
                    <td style="background-color: #ffffff; padding: 32px 40px 24px 40px; border-bottom: 1px solid #e5e7eb;">
                        <table role="presentation" style="width: 100%;">
                            <tr>
                                <td align="left">
                                    <div style="display: flex; align-items: center;">
                                        <!-- Leaf Icon -->
                                        <svg width="40" height="40" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg" style="margin-right: 12px;">
                                            <path d="M11 2L13.3 8.2C14.1 10.5 15.8 12.3 18 13.2L22 14.5C22.8 14.8 22.8 15.9 22 16.2L18 17.5C15.8 18.4 14.1 20.2 13.3 22.5L11 28.7C10.7 29.4 9.3 29.4 9 28.7L6.7 22.5C5.9 20.2 4.2 18.4 2 17.5L-2 16.2C-2.8 15.9 -2.8 14.8 -2 14.5L2 13.2C4.2 12.3 5.9 10.5 6.7 8.2L9 2C9.3 1.3 10.7 1.3 11 2Z" fill="#16a34a"/>
                                        </svg>
                                        <h1 style="margin: 0; font-size: 28px; font-weight: bold; color: #15803d; line-height: 1.2;">
                                            GreenYP
                                        </h1>
                                    </div>
                                    <p style="margin: 8px 0 0 0; font-size: 14px; color: #6b7280; line-height: 1.4;">
                                        Buy and sell lawn, garden, and agricultural equipment and services.
                                    </p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <!-- Main Content -->
                <tr>
                    <td style="padding: 40px;">
                        <p style="font-size: 14px; color: #6b7280; line-height: 1.4;">
                            Dear ${firstName} ${lastName},
                        </p>
                        <p style="font-size: 14px; color: #6b7280; line-height: 1.4;">
                            You or someone you know is attempting to place a classified ad with us for
                            30 days at the cost of $${paymentAmount} in the ${categoryName} for
                            ${classifiedTitle}.
                        </p>
                        <p style="font-size: 14px; color: #6b7280; line-height: 1.4;">
                            The confirmation code for the payment page is <br/><br/>
                            <strong>${emailValidationToken}</strong>.
                            <br/><br/>Enter this value on the Green Market classified payment page to confirm your identity.
                        </p>
                        <br/><br/>
                        <p style="font-size: 14px; color: #6b7280; line-height: 1.4;">
                            The classified ad was placed from ip address ${ipAddress} at ${timestamp}.
                        </p>
                    </td>
                </tr>

                <!-- Footer -->
                <tr>
                    <td style="background-color: #f9fafb; padding: 32px 40px; border-top: 1px solid #e5e7eb;">
                        <table role="presentation" style="width: 100%;">
                            <tr>
                                <td align="center">
                                    <p style="margin: 0 0 16px 0; font-size: 16px; font-weight: 600; color: #374151;">
                                        GreenYP - Classifieds
                                    </p>
                                    <p style="margin: 0 0 24px 0; font-size: 14px; color: #6b7280; line-height: 1.5;">
                                        Your marketplace for lawn, garden, and agricultural equipment and services.
                                    </p>
                                    <!-- CTA Button -->
                                    <div style="margin-bottom: 24px;">
                                        <a href="https://greenyp.com/classifieds" style="display: inline-block; background-color: #16a34a; color: #ffffff; font-size: 16px; font-weight: 600; text-decoration: none; padding: 12px 24px; border-radius: 6px; border: none;">
                                            Visit GreenYP Classifieds
                                        </a>
                                    </div>
                                    <!-- Links -->
                                    <div style="margin-bottom: 16px;">
                                        <a href="mailto:support@greenyp.com" style="color: #16a34a; font-size: 14px; text-decoration: none; margin-right: 16px;">
                                            Contact Support
                                        </a>
                                        <a href="https://greenyp.com/terms" style="color: #6b7280; font-size: 14px; text-decoration: none; margin-right: 16px;">
                                            Terms
                                        </a>
                                        <a href="https://greenyp.com/privacy" style="color: #6b7280; font-size: 14px; text-decoration: none;">
                                            Privacy
                                        </a>
                                    </div>
                                    <p style="margin: 0; font-size: 12px; color: #9ca3af;">
                                        © ${.now?string["yyyy"]} GreenYP. All rights reserved.
                                    </p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
