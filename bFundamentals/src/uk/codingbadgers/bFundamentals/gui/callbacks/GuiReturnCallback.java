package uk.codingbadgers.bFundamentals.gui.callbacks;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import uk.codingbadgers.bFundamentals.gui.GuiCallback;
import uk.codingbadgers.bFundamentals.gui.GuiInventory;

public class GuiReturnCallback implements GuiCallback {

	private final GuiInventory m_previousMenu;
	
	public GuiReturnCallback(GuiInventory previousMenu) {
		m_previousMenu = previousMenu;
	}
	
	@Override
	public void onClick(GuiInventory inventory, InventoryClickEvent clickEvent) {
		m_previousMenu.open((Player)clickEvent.getWhoClicked());
	}

}
