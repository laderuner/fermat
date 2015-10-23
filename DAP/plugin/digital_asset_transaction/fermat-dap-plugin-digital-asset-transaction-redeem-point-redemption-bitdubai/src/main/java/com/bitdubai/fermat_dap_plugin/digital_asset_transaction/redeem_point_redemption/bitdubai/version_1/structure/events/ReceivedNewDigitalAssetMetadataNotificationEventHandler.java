package com.bitdubai.fermat_dap_plugin.digital_asset_transaction.redeem_point_redemption.bitdubai.version_1.structure.events;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventHandler;
import com.bitdubai.fermat_api.layer.dmp_transaction.TransactionServiceNotStartedException;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantSetObjectException;
import com.bitdubai.fermat_dap_api.layer.all_definition.util.Validate;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.CantSaveEventException;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.event_manager.events.ReceivedNewDigitalAssetMetadataNotificationEvent;

/**
 * Created by Víctor A. Mars M. (marsvicam@gmail.com) on 23/10/15.
 */
public class ReceivedNewDigitalAssetMetadataNotificationEventHandler implements FermatEventHandler {

    //VARIABLE DECLARATION
    private RedeemPointRedemptionRecorderService recorderService;

    //CONSTRUCTORS

    public ReceivedNewDigitalAssetMetadataNotificationEventHandler(RedeemPointRedemptionRecorderService recorderService) throws CantSetObjectException {
        this.recorderService = Validate.verifySetter(recorderService, "recorderService is null");
    }

    //PUBLIC METHODS
    @Override
    public void handleEvent(FermatEvent fermatEvent) throws FermatException {
        if (!(fermatEvent instanceof ReceivedNewDigitalAssetMetadataNotificationEvent)) {
            throw new CantSaveEventException(null, "Handling the ReceivedNewDigitalAssetMetadataNotificationEvent", "Illegal Argument, this method takes an ReceivedNewDigitalAssetMetadataNotificationEvent and was passed an : " + fermatEvent.getClass().getName());
        }
        if (recorderService.getStatus() != ServiceStatus.STARTED) {
            throw new TransactionServiceNotStartedException();
        }
        recorderService.receivedNewDigitalAssetMetadataNotificationEvent((ReceivedNewDigitalAssetMetadataNotificationEvent) fermatEvent);
    }

    //PRIVATE METHODS

    //GETTERS AND SETTERS

    //INNER CLASSES
}
