package com.bitdubai.fermat_core;

import com.bitdubai.fermat_api.layer.all_definition.common.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.exceptions.CantListReferencesException;
import com.bitdubai.fermat_api.layer.all_definition.common.exceptions.CyclicalRelationshipFoundException;
import com.bitdubai.fermat_api.layer.all_definition.common.exceptions.VersionNotFoundException;
import com.bitdubai.fermat_api.layer.all_definition.common.utils.PluginVersionReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The class <code>com.bitdubai.fermat_core.FermatPluginReferencesCalculator</code>
 * is responsible for the injection of the needed references for the plugins.
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 22/10/2015.
 */
public class FermatPluginReferencesCalculator {

    private final Map<PluginVersionReference, Integer> pluginLevels;

    private final FermatSystemContext fermatSystemContext;

    public FermatPluginReferencesCalculator(final FermatSystemContext fermatSystemContext) {

        this.pluginLevels        = new HashMap<>();

        this.fermatSystemContext = fermatSystemContext;
    }

    public final List<PluginVersionReference> listReferencesOrdered(final PluginVersionReference pluginVersionReference) throws CantListReferencesException {

        try {
            setLevels(pluginVersionReference, 1);
        } catch (VersionNotFoundException e) {

            throw new CantListReferencesException(
                    e,
                    pluginVersionReference.toString(),
                    "Plugin version not found."
            );
        } catch(CyclicalRelationshipFoundException e) {

            throw new CantListReferencesException(
                    e,
                    pluginVersionReference.toString(),
                    "Cyclical relationship found between the references of the requested plugin version."
            );
        } catch (Exception e) {

            throw new CantListReferencesException(
                    e,
                    pluginVersionReference.toString(),
                    "Unhandled error trying to get the order of the references of the given plugin version."
            );
        }

        return getPluginsInstantiationOrder();
    }

    private List<PluginVersionReference> getPluginsInstantiationOrder() {

        List<Map.Entry<PluginVersionReference, Integer>> list = new LinkedList<>(pluginLevels.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<PluginVersionReference, Integer>>() {
            public int compare(Map.Entry<PluginVersionReference, Integer> o1, Map.Entry<PluginVersionReference, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        List<PluginVersionReference> orderedList = new ArrayList<>();

        for (Map.Entry<PluginVersionReference, Integer> entry : list)
            orderedList.add(entry.getKey());

        return orderedList;
    }


    private void setLevels(PluginVersionReference prToCalc, Integer lvlToAssign) throws VersionNotFoundException, CyclicalRelationshipFoundException {

        Integer lvlToAssignToReferences = lvlToAssign + 1;

        AbstractPlugin abstractPlugin = fermatSystemContext.getPluginVersion(prToCalc);

        if(pluginLevels.containsKey(prToCalc)) {

            Integer actualLvl = pluginLevels.get(prToCalc);

            if (actualLvl < lvlToAssign) {
                pluginLevels.put(prToCalc, lvlToAssign);
                assignToReferences(abstractPlugin, lvlToAssignToReferences);
            } else if (actualLvl.equals(lvlToAssign)) {
                assignToReferences(abstractPlugin, lvlToAssignToReferences);
            } else {
                lvlToAssignToReferences = actualLvl + 1;
                assignToReferences(abstractPlugin, lvlToAssignToReferences);
            }

        } else {
            pluginLevels.put(prToCalc, lvlToAssign);
            assignToReferences(abstractPlugin, lvlToAssignToReferences);
        }

    }

    private void setLevels(final PluginVersionReference prToCalc,
                           final List<PluginVersionReference> pluginReferenceList,
                           final Integer lvlToAssign) throws VersionNotFoundException, CyclicalRelationshipFoundException {

        Integer lvlToAssignToReferences = lvlToAssign + 1;

        if(pluginLevels.containsKey(prToCalc)) {

            Integer actualLvl = pluginLevels.get(prToCalc);

            if (actualLvl < lvlToAssign) {
                pluginLevels.put(prToCalc, lvlToAssign);
                assignToReferences(prToCalc, pluginReferenceList, lvlToAssignToReferences);
            } else if (actualLvl.equals(lvlToAssign)) {
                assignToReferences(prToCalc, pluginReferenceList, lvlToAssignToReferences);
            } else {
                lvlToAssignToReferences = actualLvl + 1;
                assignToReferences(prToCalc, pluginReferenceList, lvlToAssignToReferences);
            }

        } else {
            pluginLevels.put(prToCalc, lvlToAssign);
            assignToReferences(prToCalc, pluginReferenceList, lvlToAssignToReferences);
        }

    }

    private void assignToReferences(final AbstractPlugin prToCalc,
                                    final Integer lvlToAssign) throws VersionNotFoundException, CyclicalRelationshipFoundException  {

        List<PluginVersionReference> refNeededList = prToCalc.getNeededPluginReferences();
        for (PluginVersionReference refNeeded : refNeededList) {
            List<PluginVersionReference> refNeededReferenceList = fermatSystemContext.getPluginVersion(refNeeded).getNeededPluginReferences();
            setLevels(refNeeded, refNeededReferenceList, lvlToAssign);
        }
    }
    private void assignToReferences(final PluginVersionReference       prToCalc           ,
                                    final List<PluginVersionReference> pluginReferenceList,
                                    final Integer                      lvlToAssign        ) throws VersionNotFoundException           ,
                                                                                                   CyclicalRelationshipFoundException {

        for (final PluginVersionReference refNeeded : pluginReferenceList) {
            List<PluginVersionReference> refNeededReferenceList = fermatSystemContext.getPluginVersion(refNeeded).getNeededPluginReferences();
            if (compareReferences(prToCalc, refNeeded, refNeededReferenceList))
                setLevels(refNeeded, refNeededReferenceList, lvlToAssign);
        }
    }

    /**
     * Throw the method <code>compareReferences</code> you can check if there is a cyclical relationship between the plugin version and its references.
     *
     * @param referenceAnalyzing       reference that we're watching.
     * @param subReferenceAnalized     reference of the reference that we're watching.
     * @param subReferenceReferences   sub-references of that reference.
     *
     * @return boolean indicating if its all ok, only false is shown. if there is a cyclical relationship found is thrown an exception.
     *
     * @throws CyclicalRelationshipFoundException if exists a cyclical redundancy.
     */
    private boolean compareReferences(final PluginVersionReference       referenceAnalyzing    ,
                                      final PluginVersionReference       subReferenceAnalized  ,
                                      final List<PluginVersionReference> subReferenceReferences) throws CyclicalRelationshipFoundException {

        for (final PluginVersionReference ref2 : subReferenceReferences) {
            if (referenceAnalyzing.equals(ref2))
                throw new CyclicalRelationshipFoundException(
                        "Comparing: " + referenceAnalyzing + "\n with: " + subReferenceAnalized,
                        "Cyclical relationship found."
                );
        }

        return false;
    }
}