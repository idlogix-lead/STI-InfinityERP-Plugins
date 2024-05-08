/******************************************************************************
 * Copyright (C) 2019 Martin Sch�nbeck Beratungen GmbH  					  *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.eri.factories;

import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;
import org.eri.processes.CopyBomProcess;

public class CopyBomProcessFactory implements IProcessFactory {

	@Override
	public ProcessCall newProcessInstance(String className) {
		// TODO Auto-generated method stub
		if ("org.eri.processes.CopyBomProcess".equals(className)) {
			return new CopyBomProcess();
			
		}
		return null;
	}

}
