package de.abas.abex.erp;

import java.util.List;

import de.abas.erp.db.DbContext;
import de.abas.erp.db.Query;
import de.abas.erp.db.schema.company.CompanyData;
import de.abas.erp.db.selection.SelectionBuilder;

public class CompanyDataDAO {

	/**
	 * 
	 * @param ctx
	 * @return
	 * @throws OperatingRecordException
	 */
	public CompanyData getCompanyData(DbContext ctx) throws OperatingRecordException {
		SelectionBuilder<CompanyData> selectionBuilder = SelectionBuilder.create(CompanyData.class);
		Query<CompanyData> query = ctx.createQuery(selectionBuilder.build());
		List<CompanyData> resultSet = query.execute();

		if (!resultSet.isEmpty()) {
			return resultSet.get(0);
		} else {
			throw new OperatingRecordException();
		}
	}
}
