package de.abas.abex.eventhandler;

import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

import de.abas.erp.db.schema.transaction.TaskHeaderEditor;
import de.abas.erp.axi.screen.ScreenControl;
import de.abas.erp.axi2.event.ButtonEvent;

import java.util.ResourceBundle;

import de.abas.abex.TaskHandler;
import de.abas.abex.Util;
import de.abas.erp.api.gui.TextBox;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.db.DbContext;
import de.abas.erp.axi2.type.ButtonEventType;

@RunFopWith(EventHandlerRunner.class)
@EventHandler(head = TaskHeaderEditor.class)
public class TaskHeaderEventHandler {

	@ButtonEventHandler(field = "yexchgexport", type = ButtonEventType.AFTER)
	public void yexchgexportButtonAfter(ButtonEvent event, ScreenControl screenControl, DbContext dbContext,
			TaskHeaderEditor head) throws EventException {

		ResourceBundle langBundle = Util.getInstance().getLanguageBundle();

		try {
			TaskHandler taskHandler = new TaskHandler(dbContext);
			taskHandler.exportTask(head);

		} catch (Exception ex) {
			TextBox tb = new TextBox(dbContext, langBundle.getString("INFOBOXHEADER"), langBundle.getString(ex.getMessage()));
			tb.show();
			return;
		}
	}
}
