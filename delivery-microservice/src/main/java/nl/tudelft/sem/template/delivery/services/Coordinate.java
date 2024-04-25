package nl.tudelft.sem.template.delivery.services;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class Coordinate {
    private final double latitude;
    private final double longitude;
}
