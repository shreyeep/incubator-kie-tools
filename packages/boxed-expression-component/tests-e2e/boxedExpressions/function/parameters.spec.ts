/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { test, expect } from "../../__fixtures__/base";

test.describe("Boxed Function parameters", () => {
  test("should add a function parameter and change its name and type", async ({ bee, stories }) => {
    await stories.openBoxedFunction();

    await bee.expression.asFunction().addParameter({ name: "name", dataType: "string", closeAs: "Escape" });

    expect(await bee.expression.asFunction().parameters.textContent()).toEqual("(name: (string))");
  });

  test("should multiple function parameter and change its name and type", async ({ bee, stories }) => {
    await stories.openBoxedFunction();

    await bee.expression.asFunction().addParameter({ name: "name", dataType: "string" });
    await bee.expression.asFunction().addParameter({ name: "age", dataType: "number" });
    await bee.expression.asFunction().addParameter({ name: "maried", dataType: "boolean" });

    expect(await bee.expression.asFunction().parameters.textContent()).toEqual(
      "(name: (string), age: (number), maried: (boolean))"
    );
  });

  test("should add function parameters and delete them", async ({ bee, stories }) => {
    await stories.openBoxedFunction();

    await bee.expression.asFunction().addParameter({ name: "name", dataType: "string" });
    await bee.expression.asFunction().addParameter({ name: "age", dataType: "number" });
    await bee.expression.asFunction().addParameter({ name: "maried", dataType: "boolean" });

    await bee.expression.asFunction().deleteParameter({ nth: 0 });

    expect(await bee.expression.asFunction().parameters.textContent()).toEqual("(age: (number), maried: (boolean))");
  });
});
