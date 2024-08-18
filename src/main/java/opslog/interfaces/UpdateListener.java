package opslog.interfaces;

public interface UpdateListener {
	void beforeUpdate(String listName);
	void afterUpdate(String listName);

	/* Example usage
	@Override
		public void beforeUpdate(String listName) {
			switch (listName) {
				case "ParentList":
					Parent selectedParent = parent_Selection_ComboBox.getSelectionModel().getSelectedItem();
					break;
				case "ChildList":
					Child selectedChild = other_Asset_ComboBox.getSelectionModel().getSelectedItem();
					break;
			}
		}

		@Override
		public void afterUpdate(String listName) {
			switch (listName) {
				case "ParentList":
					// Restore the previous selection for ParentList
					Parent selectedParent = parent_Selection_ComboBox.getSelectionModel().getSelectedItem();
					if (selectedParent != null && ParentManager.getParentList().contains(selectedParent)) {
						parent_Selection_ComboBox.getSelectionModel().select(selectedParent);
					}
					break;
				case "ChildList":
					// Restore the previous selection for OtherAssetList
					Child selectedChild = Child_Selection_ComboBox.getSelectionModel().getSelectedItem();
					if (selectedChild != null && OtherAssetManager.getAssetList().contains(selectedChild)) {
						other_Asset_ComboBox.getSelectionModel().select(selectedChild);
					}
					break;
			}
		}
	*/
}