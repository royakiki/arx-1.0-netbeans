/*
 * FLASH: Efficient, Stable and Optimal Data Anonymization
 * Copyright (C) 2012 - 2013 Florian Kohlmayer, Fabian Prasser
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.deidentifier.flash.framework.check.transformer;

import org.deidentifier.flash.framework.Configuration;
import org.deidentifier.flash.framework.check.distribution.IntArrayDictionary;
import org.deidentifier.flash.framework.data.GeneralizationHierarchy;

/**
 * The class Transformer05.
 * 
 * @author Prasser, Kohlmayer
 */
public class Transformer05 extends AbstractTransformer {

    /**
     * Instantiates a new transformer.
     * 
     * @param data
     *            the data
     * @param hierarchies
     *            the hierarchies
     */
    public Transformer05(final int[][] data,
                         final GeneralizationHierarchy[] hierarchies,
                         final int[] sensitiveValues,
                         final IntArrayDictionary dictionarySensValue,
                         final IntArrayDictionary dictionarySensFreq,
                         final Configuration config) {
        super(data,
              hierarchies,
              sensitiveValues,
              dictionarySensValue,
              dictionarySensFreq,
              config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deidentifier.flash.framework.check.transformer.AbstractTransformer
     * #walkAll()
     */
    @Override
    protected void processAll() {
        for (int i = startIndex; i < stopIndex; i++) {
            intuple = data[i];
            outtuple = buffer[i];
            outtuple[outindex0] = idindex0[intuple[index0]][stateindex0];
            outtuple[outindex1] = idindex1[intuple[index1]][stateindex1];
            outtuple[outindex2] = idindex2[intuple[index2]][stateindex2];
            outtuple[outindex3] = idindex3[intuple[index3]][stateindex3];
            outtuple[outindex4] = idindex4[intuple[index4]][stateindex4];
            switch (config.getCriterion()) {
            case K_ANONYMITY:
                groupify.add(outtuple, i, 1);
                break;
            case L_DIVERSITY:
            case T_CLOSENESS:
                groupify.add(outtuple, i, 1, sensitiveValues[i]);
                break;
            default:
                throw new UnsupportedOperationException(config.getCriterion() +
                                                        ": currenty not supported");
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deidentifier.flash.framework.check.transformer.AbstractTransformer
     * #walkGroupify ()
     */
    @Override
    protected void processGroupify() {
        int processed = 0;
        while (element != null) {

            intuple = data[element.representant];
            outtuple = buffer[element.representant];
            outtuple[outindex0] = idindex0[intuple[index0]][stateindex0];
            outtuple[outindex1] = idindex1[intuple[index1]][stateindex1];
            outtuple[outindex2] = idindex2[intuple[index2]][stateindex2];
            outtuple[outindex3] = idindex3[intuple[index3]][stateindex3];
            outtuple[outindex4] = idindex4[intuple[index4]][stateindex4];
            switch (config.getCriterion()) {
            case K_ANONYMITY:
                groupify.add(outtuple, element.representant, element.count);
                break;
            case L_DIVERSITY:
            case T_CLOSENESS:
                groupify.add(outtuple,
                             element.representant,
                             element.count,
                             element.distribution);
                break;
            default:
                throw new UnsupportedOperationException(config.getCriterion() +
                                                        ": currenty not supported");
            }

            // Next element
            processed++;
            if (processed == numElements) { return; }
            element = element.nextOrdered;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deidentifier.flash.framework.check.transformer.AbstractTransformer
     * #walkSnapshot ()
     */
    @Override
    protected void processSnapshot() {
        startIndex *= ssStepWidth;
        stopIndex *= ssStepWidth;

        for (int i = startIndex; i < stopIndex; i += ssStepWidth) {
            intuple = data[snapshot[i]];
            outtuple = buffer[snapshot[i]];
            outtuple[outindex0] = idindex0[intuple[index0]][stateindex0];
            outtuple[outindex1] = idindex1[intuple[index1]][stateindex1];
            outtuple[outindex2] = idindex2[intuple[index2]][stateindex2];
            outtuple[outindex3] = idindex3[intuple[index3]][stateindex3];
            outtuple[outindex4] = idindex4[intuple[index4]][stateindex4];
            switch (config.getCriterion()) {
            case K_ANONYMITY:
                groupify.add(outtuple, snapshot[i], snapshot[i + 1]);
                break;
            case L_DIVERSITY:
            case T_CLOSENESS:
                groupify.add(outtuple,
                             snapshot[i],
                             snapshot[i + 1],
                             dictionarySensValue.get(snapshot[i + 2]),
                             dictionarySensFreq.get(snapshot[i + 3]));
                break;
            default:
                throw new UnsupportedOperationException(config.getCriterion() +
                                                        ": currenty not supported");
            }
        }
    }
}
