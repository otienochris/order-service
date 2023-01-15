package ke.or.expd.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryInquiryResponse {

    private String skuCode;
    private Boolean isInStock;
}
