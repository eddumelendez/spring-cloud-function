/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.function.adapter.gcp;

import java.util.Collection;
import java.util.function.Function;

import com.google.api.services.cloudfunctions.v1.model.CloudFunction;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.cloud.function.context.AbstractSpringFunctionAdapterInitializer;

/**
 *
 * @param <I> input type
 * @param <O> output type
 * @author Eddú Meléndez
 */
public class GcpSpringBootRequestHandler<I, O> extends AbstractSpringFunctionAdapterInitializer<CloudFunction> {

	public O handleRequest(I input, CloudFunction cloudFunction) {
		String name = null;
		if (cloudFunction != null) {
			name = cloudFunction.getName();
			initialize(cloudFunction);

			Publisher<?> events = input == null ? Mono.empty() : extract(convertEvent(input));
		}
		return null;
	}

	protected Flux<?> extract(Object input) {
		if (!isSingleInput(this.getFunction(), input)) {
			return Flux.fromIterable((Iterable<?>) input);
		}
		return Flux.just(input);
	}

	protected boolean isSingleInput(Function<?, ?> function, Object input) {
		if (!(input instanceof Collection)) {
			return true;
		}
		if (getInspector() != null) {
			return Collection.class
				.isAssignableFrom(getInspector().getInputType(function));
		}
		return ((Collection<?>) input).size() <= 1;
	}

	protected Object convertEvent(I input) {
		return input;
	}
}
