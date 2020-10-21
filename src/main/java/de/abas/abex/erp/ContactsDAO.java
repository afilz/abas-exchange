package de.abas.abex.erp;

import java.util.List;

import de.abas.erp.db.DbContext;
import de.abas.erp.db.EditorAction;
import de.abas.erp.db.Query;
import de.abas.erp.db.exception.CommandException;
import de.abas.erp.db.schema.customer.CustomerContact;
import de.abas.erp.db.schema.customer.CustomerContactEditor;
import de.abas.erp.db.schema.customer.ProspectContact;
import de.abas.erp.db.schema.customer.ProspectContactEditor;
import de.abas.erp.db.schema.vendor.VendorContact;
import de.abas.erp.db.schema.vendor.VendorContactEditor;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;

public class ContactsDAO {

	private DbContext dbContext = null;

	public ContactsDAO(DbContext dbContext) {
		this.dbContext = dbContext;
	}

	public List<CustomerContact> getCustomersContactList(boolean resigned) {
		SelectionBuilder<CustomerContact> selectionBuilder = SelectionBuilder.create(CustomerContact.class);
		selectionBuilder.add(Conditions.eq(CustomerContact.META.yexchgresigned, resigned));
		selectionBuilder.add(Conditions.eq(CustomerContact.META.yexchgexported, false));
		Query<CustomerContact> query = dbContext.createQuery(selectionBuilder.build());
		List<CustomerContact> resultSet = query.execute();

		return resultSet;
	}

	public List<ProspectContact> getProspectsContactList(boolean resigned) {
		SelectionBuilder<ProspectContact> selectionBuilder = SelectionBuilder.create(ProspectContact.class);
		selectionBuilder.add(Conditions.eq(ProspectContact.META.yexchgresigned, resigned));
		selectionBuilder.add(Conditions.eq(ProspectContact.META.yexchgexported, false));
		Query<ProspectContact> query = dbContext.createQuery(selectionBuilder.build());
		List<ProspectContact> resultSet = query.execute();

		return resultSet;
	}

	public List<VendorContact> getVendorsContactList(boolean resigned) {
		SelectionBuilder<VendorContact> selectionBuilder = SelectionBuilder.create(VendorContact.class);
		selectionBuilder.add(Conditions.eq(VendorContact.META.yexchgresigned, resigned));
		selectionBuilder.add(Conditions.eq(VendorContact.META.yexchgexported, false));
		Query<VendorContact> query = dbContext.createQuery(selectionBuilder.build());
		List<VendorContact> resultSet = query.execute();

		return resultSet;
	}

	public void confirmCustomerContactExported(CustomerContact contact) {
		try {
			CustomerContactEditor editor = contact.createEditor();
			editor.open(EditorAction.UPDATE);

			editor.setYexchgexported(true);
			editor.commit();

		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void confirmProspectContactExported(ProspectContact contact) {
		try {
			ProspectContactEditor editor = contact.createEditor();
			editor.open(EditorAction.UPDATE);
			editor.setYexchgexported(true);
			editor.commit();
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void confirmVendorContactExported(VendorContact contact) {
		try {
			VendorContactEditor editor = contact.createEditor();
			editor.open(EditorAction.UPDATE);
			editor.setYexchgexported(true);
			editor.commit();
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
