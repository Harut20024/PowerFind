package com.powerfind.model.data;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audit
{

    private UUID id;
    private String action;
    private String tableName;
    private String method;
    private JsonNode data;
}
