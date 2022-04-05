/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com._5icodes.starter.sharding;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import groovy.lang.GString;
import groovy.lang.GroovyShell;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * @see io.shardingjdbc.core.util.InlineExpressionParser
 */
@RequiredArgsConstructor
public final class InlineExpressionParser {
    
    private static final char SPLITTER = ',';
    
    private final String inlineExpression;
    
    /**
     * Split and evaluate inline expression.
     *
     * @return result list
     */
    public List<String> evaluate() {
        if (null == inlineExpression) {
            return Collections.emptyList();
        }
        return flatten(evaluate(split()));
    }
    
    private List<Object> evaluate(final List<String> inlineExpressions) {
        List<Object> result = new ArrayList<>(inlineExpressions.size());
        GroovyShell shell = new GroovyShell();
        for (String each : inlineExpressions) {
            StringBuilder expression = new StringBuilder(each);
            if (!each.contains("{")) {
                result.add(each);
                continue;
            }
            if (!each.startsWith("\"")) {
                expression.insert(0, "\"");
            }
            if (!each.endsWith("\"")) {
                expression.append("\"");
            }
            result.add(shell.evaluate(expression.toString()));
        }
        return result;
    }
    
    private List<String> split() {
        List<String> result = new ArrayList<>();
        StringBuilder segment = new StringBuilder();
        int bracketsDepth = 0;
        for (int i = 0; i < inlineExpression.length(); i++) {
            char each = inlineExpression.charAt(i);
            switch (each) {
                case SPLITTER:
                    if (bracketsDepth > 0) {
                        segment.append(each);
                    } else {
                        result.add(segment.toString().trim());
                        segment.setLength(0);
                    }
                    break;
                case '$':
                    if ('{' == inlineExpression.charAt(i + 1)) {
                        bracketsDepth++;
                    }
                    segment.append(each);
                    break;
                case '}':
                    if (bracketsDepth > 0) {
                        bracketsDepth--;
                    }
                    segment.append(each);
                    break;
                default:
                    segment.append(each);
                    break;
            }
        }
        if (segment.length() > 0) {
            result.add(segment.toString().trim());
        }
        return result;
    }
    
    private List<String> flatten(final List<Object> segments) {
        List<String> result = new ArrayList<>();
        for (Object each : segments) {
            if (each instanceof GString) {
                result.addAll(assemblyCartesianSegments((GString) each));
            } else {
                result.add(each.toString());
            }
        }
        return result;
    }
    
    private List<String> assemblyCartesianSegments(final GString segment) {
        Set<List<String>> cartesianValues = getCartesianValues(segment);
        List<String> result = new ArrayList<>(cartesianValues.size());
        for (List<String> each : cartesianValues) {
            result.add(assemblySegment(each, segment));
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private Set<List<String>> getCartesianValues(final GString segment) {
        List<Set<String>> result = new ArrayList<>(segment.getValues().length);
        for (Object each : segment.getValues()) {
            if (null == each) {
                continue;
            }
            if (each instanceof Collection) {
                result.add(Sets.newLinkedHashSet(Collections2.transform((Collection<Object>) each, new Function<Object, String>() {
                    
                    @Override
                    public String apply(final Object input) {
                        return input.toString();
                    }
                })));
            } else {
                result.add(Sets.newHashSet(each.toString()));
            }
        }
        return Sets.cartesianProduct(result);
    }
    
    private String assemblySegment(final List<String> cartesianValue, final GString segment) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < segment.getStrings().length; i++) {
            result.append(segment.getStrings()[i]);
            if (i < cartesianValue.size()) {
                result.append(cartesianValue.get(i));
            }
        }
        return result.toString();
    }
}
