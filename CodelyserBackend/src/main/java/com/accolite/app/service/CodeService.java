package com.accolite.app.service;


import com.accolite.app.dto.CodeDTO;

import java.io.IOException;
import java.util.List;

public interface CodeService {
    public List<String> submitCode(CodeDTO code) throws IOException, InterruptedException;
}
