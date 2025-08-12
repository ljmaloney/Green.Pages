update line_of_business
set line_of_business='Cleanup & Debris Removal',
    short_description='Remove yard waste, overgrown vegetation, storm debris, junk, trash, and other unwanted items from residential or commercial properties',
    description = 'Clearing and removing unwanted materials, junk, trash, and other debris from outdoor spaces helps keep properties safe, functional, and presentable. This includes the disposal of leaves, branches, overgrown plants, fallen trees, or bulk waste. This may be seasonally as a part of routine maintenance, or needed after storms or other events. By removing hazards and improving the appearance of a property, outdoor cleanup makes yards, gardens, and shared spaces more usable and welcoming.'
where (line_of_business='Cleanup' or id = uuid_to_bin('3d81418b-c435-47dd-8272-3c8bcd49d1d3'));

update lob_service
set  service_name = 'Yard and Property Cleanup',
     service_description = 'Removal of overgrown vegetation, natural debris, and unwanted items from outdoor spaces. This may include raking and bagging leaves, trimming or cutting back brush, pulling weeds, and clearing pathways or garden areas. In many cases, crews also handle heavier work such as cutting and removing fallen branches or dismantling old outdoor structures that are no longer needed.'
where id = uuid_to_bin('0de195e0-6f14-11f0-b5a8-5a855ca00917');

update lob_service
set  service_name='Storm Debris Removal',
     service_description='After severe weather, properties can be left with downed trees, broken limbs, and scattered materials. Storm cleanup focuses on safely removing these hazards to restore access and prevent further damage. This often includes cutting large branches into manageable pieces, stacking or hauling materials away, and checking for debris that could block drainage or pose safety risks.'
where id = uuid_to_bin('0de1c42f-6f14-11f0-b5a8-5a855ca00917');

update lob_service
set  service_name='Haul-Away and Disposal',
     service_description='Load and transport trash, junk, and other materials to approved disposal or recycling facilities. This is especially useful when preparing a property for sale, clearing space for new landscaping, or meeting community maintenance requirements.'
where id = uuid_to_bin('0de1f174-6f14-11f0-b5a8-5a855ca00917');