package com.powerfind.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powerfind.model.data.Audit;
import com.powerfind.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect
{

    private final AuditRepository auditRepo;
    private final ObjectMapper mapper;

    @Around("@annotation(auditable)")
    public Object record(ProceedingJoinPoint pjp, Auditable auditable) throws Throwable
    {

        Object result = pjp.proceed();

        JsonNode payload = switch (auditable.action())
        {
            case "READ" -> null;
            case "INSERT", "UPDATE", "DELETE" -> mapper.valueToTree(pjp.getArgs());
            default -> null;
        };

        auditRepo.save(Audit.builder()
                .id(UUID.randomUUID())
                .action(auditable.action())
                .tableName(auditable.table())
                .method(pjp.getSignature().toShortString())
                .data(payload)
                .build());

        return result;
    }

}
