/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.configmanager.extensions.routes.petrinet.evaluation.formula.transition;

import io.configmanager.extensions.routes.petrinet.model.Node;
import io.configmanager.extensions.routes.petrinet.model.Transition;
import io.configmanager.extensions.routes.petrinet.simulator.PetriNetSimulator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates to true, if on any possible path every transition fulfills parameter1,
 * until a transition fulfills parameter2.
 */
@Log4j2
@AllArgsConstructor
public class TransitionFORALLUNTIL implements TransitionFormula {
    /**
     * On any possible path every transition needs to fulfill this parameter.
     */
    private TransitionFormula parameter1;

    /**
     * Parameter 1 needs to be fulfilled until a transition fulfills his parameter.
     */
    private TransitionFormula parameter2;

    /**
     * Evaluates to true, if on any possible path every transition fulfills parameter1,
     * until a transition fulfills parameter2.
     * @param parameter1 On any possible path every transition needs to fulfill this parameter.
     * @param parameter2 Parameter 1 needs to be fulfilled until a transition
     *                   fulfills his parameter.
     * @return Transition representing the formula.
     */
    public static TransitionFORALLUNTIL transitionFORALLUNTIL(final TransitionFormula parameter1,
                                                              final TransitionFormula parameter2) {
        return new TransitionFORALLUNTIL(parameter1, parameter2);
    }

    //like EXIST_UNTIL but requires conditions for all paths
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        if (!(node instanceof Transition)) {
            return false;
        }

        check: for (final var path: paths) {
            if (!path.get(0).equals(node)) {
                continue;
            }
            int offset;
            if (PetriNetSimulator.circleFree(path)) {
                if (path.size() % 2 == 1) {
                    offset = 1;
                } else {
                    offset = 2;
                }
                for (var i = 2; i < path.size() - offset; i += 2) {
                    final var res1 = parameter1.evaluate(path.get(i), paths);
                    final var res2 = parameter2.evaluate(path.get(i), paths);
                    if (res2) {
                        continue check;
                    }
                    if (!res1) {
                        log.info(path.get(i).toString());
                        return false;
                    }
                }
                if (!parameter2.evaluate(path.get(path.size() - offset), paths)) {
                    if (log.isInfoEnabled()) {
                        log.info(path.get(path.size() - offset).toString());
                    }
                    return false;
                }
            } else {
                //if something on the circle fulfills param2 accept,
                //if something does not fulfill param1 reject
                for (var i = 2; i < path.size() - 1; i += 2) {
                    final var res1 = parameter1.evaluate(path.get(i), paths);
                    final var res2 = parameter2.evaluate(path.get(i), paths);
                    if (res2) {
                        continue check;
                    }
                    if (!res1) {
                        log.info(path.get(i).toString());
                        return false;
                    }
                }
                //if everything on circle fulfills param1 but not param2
                final var lastTransition = path.get(path.size() - 1) instanceof Transition
                        ? path.get(path.size() - 1)
                        : path.get(path.size() - 2);

                final var newPaths = new ArrayList<>(paths);

                newPaths.remove(path);

                if (!this.evaluate(lastTransition, newPaths)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "FORALL_UNTIL";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeFormula() {
        return String.format("%s(%s, %s)",
                symbol(),
                parameter1.writeFormula(),
                parameter2.writeFormula());
    }
}
