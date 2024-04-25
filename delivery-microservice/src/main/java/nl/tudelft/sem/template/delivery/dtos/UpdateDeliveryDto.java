package nl.tudelft.sem.template.delivery.dtos;

import delivery_microservice.model.Times;
import delivery_microservice.model.UpdateDeliveryRequest;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateDeliveryDto {
    private Long deliveryId;
    private Long courierId;
    private Integer rating;
    private Times times;
    private UpdateDeliveryRequest.StatusEnum status;
    private Boolean delivered;
}
