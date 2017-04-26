package org.gdl2.test;

import lombok.Value;

import java.util.Map;

@Value
public class TestCase {
    private String id;
    private Map<String, String> input;
    private Map<String, String> expectedOutput;
}
