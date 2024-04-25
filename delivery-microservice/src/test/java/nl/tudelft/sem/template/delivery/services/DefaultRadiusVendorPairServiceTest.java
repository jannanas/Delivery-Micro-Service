package nl.tudelft.sem.template.delivery.services;


import delivery_microservice.model.RadiusVendorPair;
import delivery_microservice.model.Location;
import nl.tudelft.sem.template.delivery.database.RadiusVendorPairRepository;
import nl.tudelft.sem.template.delivery.exceptions.LocationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

class DefaultRadiusVendorPairServiceTest {
    private RadiusVendorPairRepository radiusVendorPairRepository = mock(RadiusVendorPairRepository.class);
    private GeocodingService geoCodingService = mock(GeocodingService.class);
    private LocationRoutingService locationRoutingService = mock(LocationRoutingService.class);
    private RadiusVendorPairService radiusVendorPairService = new DefaultRadiusVendorPairService(
        radiusVendorPairRepository, geoCodingService, locationRoutingService);
    private Long vendorId = 3L;
    private Location deliveryVendorLocation = new Location();
    private Location deliveryCustomerLocation = new Location();
    private Location createdDeliveryVendorLocation = new Location();
    private Location createdDeliveryCustomerLocation = new Location();
    private Location differentDeliveryVendorLocation = new Location();
    private Location differentDeliveryCustomerLocation = new Location();
    private Location anotherDeliveryVendorLocation = new Location();
    private Location anotherDeliveryCustomerLocation = new Location();
    private RadiusVendorPair customRadiusVendorPair = new RadiusVendorPair();

    @BeforeEach
    void setup() {
        // 0.64 kilometers between vendor and customer
        // Vendor: 52.002498751165774, 4.3721623562566965
        // Customer: 52.00748892119812, 4.3683047217155915
        deliveryVendorLocation.setCountry("Netherlands");
        deliveryVendorLocation.setCity("Delft");
        deliveryVendorLocation.setAddress("Mekelweg 3");
        deliveryVendorLocation.setPostalCode("2628 CD");
        deliveryCustomerLocation.setCountry("Netherlands");
        deliveryCustomerLocation.setCity("Delft");
        deliveryCustomerLocation.setAddress("Professor Schermerhornstraat 9");
        deliveryCustomerLocation.setPostalCode("2628 CN");

        // Distance NA
        // Missing vendor location
        // Customer: 52.00748892119812, 4.3683047217155915
        createdDeliveryCustomerLocation.setCountry("Netherlands");
        createdDeliveryCustomerLocation.setCity("Delft");
        createdDeliveryCustomerLocation.setAddress("Professor Schermerhornstraat 9");
        createdDeliveryCustomerLocation.setPostalCode("2628 CN");


        // 9.23 kilometers between vendor and customer
        // Vendor: 52.002498751165774, 4.3721623562566965
        // Customer: 52.05835948712652, 4.272474229060602
        differentDeliveryVendorLocation.setCountry("Netherlands");
        differentDeliveryVendorLocation.setCity("Delft");
        differentDeliveryVendorLocation.setAddress("Mekelweg 3");
        differentDeliveryVendorLocation.setPostalCode("2628 CD");
        differentDeliveryCustomerLocation.setCountry("Netherlands");
        differentDeliveryCustomerLocation.setCity("Den Haag");
        differentDeliveryCustomerLocation.setAddress("Maarsbergenstraat 250");
        differentDeliveryCustomerLocation.setPostalCode("2546 SV");

        // 30.6 kilometers between vendor and customer
        // Vendor: 52.002498751165774, 4.3721623562566965
        // Customer: 51.81145844639746, 4.6932817709409544
        anotherDeliveryVendorLocation.setCountry("Netherlands");
        anotherDeliveryVendorLocation.setCity("Delft");
        anotherDeliveryVendorLocation.setAddress("Mekelweg 3");
        anotherDeliveryVendorLocation.setPostalCode("2628 CD");
        anotherDeliveryCustomerLocation.setCountry("Netherlands");
        anotherDeliveryCustomerLocation.setCity("Dordrecht");
        anotherDeliveryCustomerLocation.setAddress("Tijpoort 34");
        anotherDeliveryCustomerLocation.setPostalCode("3312 WB");

        customRadiusVendorPair.vendorId(3L);
        customRadiusVendorPair.radius(10000);
    }

    @Test
    void closeRangeDefaultRadiusTest() throws LocationNotFoundException {
        // Radius = 5
        // Distance = 0.64
        Coordinate customerCoordinate = new Coordinate(52.00748892119812, 4.3683047217155915);
        Coordinate vendorCoordinate = new Coordinate(52.002498751165774, 4.3721623562566965);

        when(radiusVendorPairRepository.findById(3L)).thenReturn(Optional.empty());
        when(geoCodingService.getCoordinatesFromLocation(deliveryCustomerLocation))
            .thenReturn(customerCoordinate);
        when(geoCodingService.getCoordinatesFromLocation(deliveryVendorLocation))
            .thenReturn(vendorCoordinate);
        when(locationRoutingService.calculateDistanceBetweenCoordinates(vendorCoordinate, customerCoordinate))
            .thenReturn(640);

        assertTrue(radiusVendorPairService.vendorCustomerLocationIsValid(vendorId,
            deliveryCustomerLocation, deliveryVendorLocation));
    }

    @Test
    void closeRangeCustomRadiusTest() throws LocationNotFoundException {
        // Radius = 10
        // Distance = 0.64
        Coordinate customerCoordinate = new Coordinate(52.05835948712652, 4.272474229060602);
        Coordinate vendorCoordinate = new Coordinate(52.002498751165774, 4.3721623562566965);

        when(radiusVendorPairRepository.findById(3L)).thenReturn(Optional.of(customRadiusVendorPair));
        when(geoCodingService.getCoordinatesFromLocation(differentDeliveryCustomerLocation))
            .thenReturn(customerCoordinate);
        when(geoCodingService.getCoordinatesFromLocation(differentDeliveryVendorLocation))
            .thenReturn(vendorCoordinate);
        when(locationRoutingService.calculateDistanceBetweenCoordinates(vendorCoordinate, customerCoordinate))
            .thenReturn(9230);

        assertTrue(radiusVendorPairService.vendorCustomerLocationIsValid(
            vendorId, deliveryCustomerLocation, deliveryVendorLocation));
    }

    @Test
    void midRangeDefaultRadiusTest() throws LocationNotFoundException {
        // Radius = 5
        // Distance = 9.23
        Coordinate customerCoordinate = new Coordinate(52.05835948712652, 4.272474229060602);
        Coordinate vendorCoordinate = new Coordinate(52.002498751165774, 4.3721623562566965);

        when(radiusVendorPairRepository.findById(3L)).thenReturn(Optional.empty());
        when(geoCodingService.getCoordinatesFromLocation(differentDeliveryCustomerLocation))
            .thenReturn(customerCoordinate);
        when(geoCodingService.getCoordinatesFromLocation(differentDeliveryVendorLocation))
            .thenReturn(vendorCoordinate);
        when(locationRoutingService.calculateDistanceBetweenCoordinates(vendorCoordinate, customerCoordinate))
            .thenReturn(9230);

        assertFalse(radiusVendorPairService.vendorCustomerLocationIsValid(
            vendorId, differentDeliveryCustomerLocation, differentDeliveryVendorLocation));
    }

    @Test
    void midRangeCustomRadiusTest() throws LocationNotFoundException {
        // Radius = 10
        // Distance = 9.23
        Coordinate customerCoordinate = new Coordinate(52.05835948712652, 4.272474229060602);
        Coordinate vendorCoordinate = new Coordinate(52.002498751165774, 4.3721623562566965);

        when(radiusVendorPairRepository.findById(3L)).thenReturn(Optional.of(customRadiusVendorPair));
        when(geoCodingService.getCoordinatesFromLocation(deliveryCustomerLocation))
            .thenReturn(customerCoordinate);
        when(geoCodingService.getCoordinatesFromLocation(deliveryVendorLocation))
            .thenReturn(vendorCoordinate);
        when(locationRoutingService.calculateDistanceBetweenCoordinates(vendorCoordinate, customerCoordinate))
            .thenReturn(9230);

        assertTrue(radiusVendorPairService.vendorCustomerLocationIsValid(
            vendorId, differentDeliveryCustomerLocation, differentDeliveryVendorLocation));
    }

    @Test
    void longRangeDefaultRadiusTest() throws LocationNotFoundException {
        // Radius = 5
        // Distance = 30.6
        Coordinate customerCoordinate = new Coordinate(51.81145844639746, 4.6932817709409544);
        Coordinate vendorCoordinate = new Coordinate(52.002498751165774, 4.3721623562566965);

        when(radiusVendorPairRepository.findById(3L)).thenReturn(Optional.empty());
        when(geoCodingService.getCoordinatesFromLocation(anotherDeliveryCustomerLocation))
            .thenReturn(customerCoordinate);
        when(geoCodingService.getCoordinatesFromLocation(anotherDeliveryVendorLocation))
            .thenReturn(vendorCoordinate);
        when(locationRoutingService.calculateDistanceBetweenCoordinates(vendorCoordinate, customerCoordinate))
            .thenReturn(30600);

        assertFalse(radiusVendorPairService.vendorCustomerLocationIsValid(
            vendorId, anotherDeliveryCustomerLocation, anotherDeliveryVendorLocation));
    }

    @Test
    void longRangeCustomRadiusTest() throws LocationNotFoundException {
        // Radius = 10
        // Distance = 30.6
        Coordinate customerCoordinate = new Coordinate(51.81145844639746, 4.6932817709409544);
        Coordinate vendorCoordinate = new Coordinate(52.002498751165774, 4.3721623562566965);

        when(radiusVendorPairRepository.findById(3L)).thenReturn(Optional.of(customRadiusVendorPair));
        when(geoCodingService.getCoordinatesFromLocation(anotherDeliveryCustomerLocation))
            .thenReturn(customerCoordinate);
        when(geoCodingService.getCoordinatesFromLocation(anotherDeliveryVendorLocation))
            .thenReturn(vendorCoordinate);
        when(locationRoutingService.calculateDistanceBetweenCoordinates(vendorCoordinate, customerCoordinate))
            .thenReturn(30600);

        assertFalse(radiusVendorPairService.vendorCustomerLocationIsValid(
            vendorId, anotherDeliveryCustomerLocation, anotherDeliveryVendorLocation));

    }

    @Test
    void onRadiusBorderTest() throws LocationNotFoundException {
        // Radius = 5
        // Distance = 5
        Coordinate customerCoordinate = new Coordinate(51.81145844639746, 4.6932817709409544);
        Coordinate vendorCoordinate = new Coordinate(52.002498751165774, 4.3721623562566965);

        when(radiusVendorPairRepository.findById(3L)).thenReturn(Optional.empty());
        when(geoCodingService.getCoordinatesFromLocation(anotherDeliveryCustomerLocation))
            .thenReturn(customerCoordinate);
        when(geoCodingService.getCoordinatesFromLocation(anotherDeliveryVendorLocation))
            .thenReturn(vendorCoordinate);
        when(locationRoutingService.calculateDistanceBetweenCoordinates(vendorCoordinate, customerCoordinate))
            .thenReturn(5000);

        assertTrue(radiusVendorPairService.vendorCustomerLocationIsValid(
            vendorId, anotherDeliveryCustomerLocation, anotherDeliveryVendorLocation));
    }

    @Test
    void justOverRadiusBorderTest() throws LocationNotFoundException {
        // Radius = 5
        // Distance = 5
        Coordinate customerCoordinate = new Coordinate(51.81145844639746, 4.6932817709409544);
        Coordinate vendorCoordinate = new Coordinate(52.002498751165774, 4.3721623562566965);

        when(radiusVendorPairRepository.findById(3L)).thenReturn(Optional.empty());
        when(geoCodingService.getCoordinatesFromLocation(anotherDeliveryCustomerLocation))
            .thenReturn(customerCoordinate);
        when(geoCodingService.getCoordinatesFromLocation(anotherDeliveryVendorLocation))
            .thenReturn(vendorCoordinate);
        when(locationRoutingService.calculateDistanceBetweenCoordinates(vendorCoordinate, customerCoordinate))
            .thenReturn(5001);

        assertFalse(radiusVendorPairService.vendorCustomerLocationIsValid(
            vendorId, anotherDeliveryCustomerLocation, anotherDeliveryVendorLocation));
    }

    @Test
    void noVendorLocationTest() throws LocationNotFoundException {
        Coordinate customerCoordinate = new Coordinate(52.00748892119812, 4.3683047217155915);

        when(radiusVendorPairRepository.findById(3L)).thenReturn(Optional.empty());
        when(geoCodingService.getCoordinatesFromLocation(createdDeliveryCustomerLocation))
            .thenReturn(customerCoordinate);
        when(geoCodingService.getCoordinatesFromLocation(createdDeliveryVendorLocation))
            .thenThrow(LocationNotFoundException.class);
        assertThrows(LocationNotFoundException.class, () -> {
            radiusVendorPairService.vendorCustomerLocationIsValid(
                vendorId, createdDeliveryCustomerLocation, createdDeliveryVendorLocation);
        });
    }

    @Test
    void existsTrueTest() {
        when(radiusVendorPairRepository.existsById(3L)).thenReturn(true);
        assertTrue(radiusVendorPairService.exists(3L));
    }

    @Test
    void existsFalseTest() {
        when(radiusVendorPairRepository.existsById(3L)).thenReturn(false);
        assertFalse(radiusVendorPairService.exists(3L));
    }

    @Test
    void getRadiusVendorPairSuccessTest() {
        when(radiusVendorPairRepository.findById(3L)).thenReturn(Optional.ofNullable(customRadiusVendorPair));
        assertEquals(radiusVendorPairService.getRadiusVendorPair(3L).get(), customRadiusVendorPair);
    }

    @Test
    void getRadiusVendorPairFailureTest() {
        when(radiusVendorPairRepository.findById(10L)).thenReturn(Optional.empty());
        when(radiusVendorPairRepository.findById(-1L)).thenReturn(Optional.empty());
        assertTrue(radiusVendorPairService.getRadiusVendorPair(3L).isEmpty());
    }

    @Test
    void getRadiusVendorPairDefaultTest() {
        when(radiusVendorPairRepository.findById(10L)).thenReturn(Optional.empty());
        when(radiusVendorPairRepository.findById(-1L)).thenReturn(Optional.ofNullable(customRadiusVendorPair));
        assertEquals(customRadiusVendorPair, radiusVendorPairService.getRadiusVendorPair(3L).get());
    }

    @Test
    void saveRadiusVendorPairTest() {
        when(radiusVendorPairRepository.save(customRadiusVendorPair)).thenReturn(customRadiusVendorPair);
        assertEquals(radiusVendorPairService.saveRadiusVendorPair(customRadiusVendorPair), customRadiusVendorPair);
        verify(radiusVendorPairRepository, times(1)).save(customRadiusVendorPair);
    }

    @Test
    void getAllRadiusVendorPairsTest() {
        List<RadiusVendorPair> radiusVendorPairList = new ArrayList<>();
        radiusVendorPairList.add(customRadiusVendorPair);

        when(radiusVendorPairRepository.findAll()).thenReturn(radiusVendorPairList);
        assertEquals(radiusVendorPairService.getAllRadiusVendorPairs(), radiusVendorPairList);
    }
}