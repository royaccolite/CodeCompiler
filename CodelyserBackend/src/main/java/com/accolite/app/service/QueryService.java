package com.accolite.app.service;

import com.accolite.app.dto.CodeDTO;

import java.io.IOException;
import java.util.List;

public interface QueryService {
    public List<Object> submitSQlQuery(CodeDTO code) throws IOException, InterruptedException;
}
