package com.switchvov.magicsharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order entity.
 *
 * @author switch
 * @since 2024/8/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private int id;
    private int uid;
    private double price;
}
