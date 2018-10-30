/**
 * This file is part of choco-solver, http://choco-solver.org/
 *
 * Copyright (c) 2018, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.unary;

import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.events.IntEventType;
import org.chocosolver.util.ESat;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableSetUtils;

/**
 * This propagator manages a singleton nogood.
 * <p>
 * <p>
 * Project: choco-solver.
 *
 * @author Charles Prud'homme
 * @since 12/10/2016.
 */
public class PropNotin extends Propagator<IntVar> {

    /**
     * List of forbidden values.
     */
    private IntIterableRangeSet range;

    /**
     * Maintain : <i>var</i>&notin;<i>range</i>
     *
     * @param var a variable
     * @param range  list of forbidden values
     */
    public PropNotin(IntVar var, IntIterableRangeSet range) {
        super(new IntVar[]{var}, PropagatorPriority.UNARY, false);
        this.range = range.duplicate();
    }

    @Override
    public int getPropagationConditions(int vIdx) {
        return IntEventType.all();
    }

    @Override
    public void propagate(int evtmask) throws ContradictionException {
        if(enforce(vars[0], range, this)){
            setPassive();
        }
    }

    private static boolean enforce(IntVar var, IntIterableRangeSet fset, Propagator<IntVar> prop) throws ContradictionException {
        return var.removeAllValuesBut(fset, prop)
                && (var.hasEnumeratedDomain() || IntIterableSetUtils.includedIn(var, fset));
    }

    @Override
    public ESat isEntailed() {
        if(IntIterableSetUtils.includedIn(vars[0], range)){
            return ESat.TRUE;
        }else if(IntIterableSetUtils.intersect(vars[0], range)){
            return ESat.UNDEFINED;
        }
        return ESat.FALSE;
    }

    @Override
    public String toString() {
        return String.valueOf(vars[0].getName()) + " \u2208 " + range;
    }
}