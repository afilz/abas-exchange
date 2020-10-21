package de.abas.abex.eventhandler;

import java.util.ResourceBundle;

import de.abas.abex.ContactHandler;
import de.abas.abex.Util;
import de.abas.erp.api.gui.TextBox;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.axi.screen.ScreenControl;
import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.annotation.ScreenEventHandler;
import de.abas.erp.axi2.event.ButtonEvent;
import de.abas.erp.axi2.type.ButtonEventType;
import de.abas.erp.axi2.type.ScreenEventType;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.vendor.VendorContactEditor;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

@EventHandler(head = VendorContactEditor.class)

@RunFopWith(EventHandlerRunner.class)

public class VendorContactEventHandler {

	@ButtonEventHandler(field = "yexchgexportbutton", type = ButtonEventType.AFTER)
	public void yexchgexportbuttonAfter(ButtonEvent event, ScreenControl screenControl, DbContext dbContext,
			VendorContactEditor head) throws EventException {

		ResourceBundle langBundle = Util.getInstance().getLanguageBundle();

		if (head.isYexchgresigned()) {
			TextBox tb = new TextBox(dbContext, langBundle.getString("INFOBOXHEADER"),
					langBundle.getString("YEXCHG_RESIGNED"));
			tb.show();
			return;
		}

		try {
			ContactHandler contactHandler = new ContactHandler(dbContext);
			contactHandler.exportContact(head);
		} catch (Exception ex) {
			TextBox tb = new TextBox(dbContext, langBundle.getString("INFOBOXHEADER"),
					langBundle.getString(ex.getMessage()));
			tb.show();
			return;
		}
	}

	/**
	 * 
	 * @param screenControl
	 * @param ctx
	 * @param head
	 * @throws EventException
	 */
	@ScreenEventHandler(type = ScreenEventType.VALIDATION)
	public void screenValidation(ScreenControl screenControl, DbContext ctx, VendorContactEditor head)
			throws EventException {
		if (!head.getYexchgresigned()) {
			head.setYexchgexported(false);
		}
	}
}
