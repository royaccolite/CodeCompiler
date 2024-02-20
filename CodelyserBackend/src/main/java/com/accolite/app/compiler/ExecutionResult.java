package com.accolite.app.compiler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class ExecutionResult {
    private final boolean success;
    private final String output;
    private final String errors;
}
