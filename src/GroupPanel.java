import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

public class GroupPanel extends JPanel {

	private static final long serialVersionUID = -8583833759512500805L;
	GroupClient gclient;
	UserToken userToken;
	@SuppressWarnings("rawtypes")
	DefaultListModel groupListModel;
	@SuppressWarnings("rawtypes")
	DefaultListModel memberListModel;
	String lastListed;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GroupPanel(final GroupClient gclient, final UserToken userToken) {
		this.gclient = gclient;
		this.userToken = userToken;
		lastListed = null;

		// Main pane. Buttons on left, lists on right.
		final JSplitPane contentPane = new JSplitPane();
		add(contentPane);

		// Setup left panel
		JPanel leftPanel = new JPanel();
		contentPane.setLeftComponent(leftPanel);
		GridBagLayout gbl_leftPanel = new GridBagLayout();
		gbl_leftPanel.columnWidths = new int[] { 130, 0 };
		gbl_leftPanel.rowHeights = new int[] { 15, 25, 25, 25, 25, 0 };
		gbl_leftPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_leftPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		leftPanel.setLayout(gbl_leftPanel);

		// Setup right panel
		groupListModel = new DefaultListModel();
		for (String group : userToken.getGroups()) {
			groupListModel.addElement(group);
		}
		final JList groupList = new JList(groupListModel);
		groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane groupListScrollPane = new JScrollPane(groupList);

		memberListModel = new DefaultListModel();
		final JList memberList = new JList(memberListModel);
		memberList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane memberListScrollPane = new JScrollPane(memberList);

		// Create Group button
		JLabel lblGroupOptions = new JLabel("Group Options");
		GridBagConstraints gbc_lblGroupOptions = new GridBagConstraints();
		gbc_lblGroupOptions.anchor = GridBagConstraints.WEST;
		gbc_lblGroupOptions.insets = new Insets(0, 0, 5, 0);
		gbc_lblGroupOptions.gridx = 0;
		gbc_lblGroupOptions.gridy = 0;
		leftPanel.add(lblGroupOptions, gbc_lblGroupOptions);

		JButton btnCreateGroup = new JButton("Create Group");
		btnCreateGroup.setActionCommand("Create Group");
		btnCreateGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String newGroupName = RunClient.askForValidInput("Enter new group name", contentPane);
				if (newGroupName != null) {
					if (groupListModel.contains(newGroupName)) {
						JOptionPane.showMessageDialog(contentPane, "Group: " + newGroupName + " already exists!");
						return;
					}
					if (!gclient.createGroup(newGroupName, userToken))
						JOptionPane.showMessageDialog(contentPane, "Failed to create group: " + newGroupName + "!");
					else {
						groupListModel.insertElementAt(newGroupName, groupListModel.size());
						memberListModel.removeAllElements();
					}
				}
				return;
			}
		});
		GridBagConstraints gbc_btnCreateGroup = new GridBagConstraints();
		gbc_btnCreateGroup.anchor = GridBagConstraints.WEST;
		gbc_btnCreateGroup.insets = new Insets(0, 0, 5, 0);
		gbc_btnCreateGroup.gridx = 0;
		gbc_btnCreateGroup.gridy = 1;
		gbc_btnCreateGroup.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(btnCreateGroup, gbc_btnCreateGroup);

		// Delete Group button
		JButton btnDeleteGroup = new JButton("Delete Group");
		btnDeleteGroup.setActionCommand("Delete Group");
		btnDeleteGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = groupList.getSelectedIndex();
				if (index != -1) {
					String groupName = groupListModel.get(index).toString();
					int reply = JOptionPane.showConfirmDialog(contentPane, "Really delete group: " + groupName,
							"WARNING", JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.YES_OPTION) {
						if (gclient.deleteGroup(groupName, userToken)) {
							groupListModel.remove(index);
							memberListModel.removeAllElements();
						} else
							JOptionPane.showMessageDialog(contentPane, "Failed to delete group: " + groupName + "!");
					}
				} else {
					JOptionPane.showMessageDialog(contentPane, "Select a group");
				}
				return;
			}
		});
		GridBagConstraints gbc_btnDeleteGroup = new GridBagConstraints();
		gbc_btnDeleteGroup.anchor = GridBagConstraints.WEST;
		gbc_btnDeleteGroup.insets = new Insets(0, 0, 5, 0);
		gbc_btnDeleteGroup.gridx = 0;
		gbc_btnDeleteGroup.gridy = 2;
		gbc_btnDeleteGroup.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(btnDeleteGroup, gbc_btnDeleteGroup);

		// Add User button
		JButton btnAddUser = new JButton("Add User");
		btnAddUser.setActionCommand("Add User");
		btnAddUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = groupList.getSelectedIndex();
				if (index == -1) {
					JOptionPane.showMessageDialog(contentPane, "Select a group");
					return;
				}
				String groupName = groupListModel.get(index).toString();
				String newUserName = RunClient.askForValidInput("Enter user's name to add to: " + groupName,
						contentPane);
				if (newUserName != null) {
					if (memberListModel.contains(newUserName)) {
						JOptionPane.showMessageDialog(contentPane, "User already belongs to: " + groupName + "!");
						return;
					}
					if (gclient.addUserToGroup(newUserName, groupName, userToken))
						memberListModel.insertElementAt(newUserName, memberListModel.size());
					else
						JOptionPane.showMessageDialog(contentPane, "Failed to add: " + newUserName + " to: "
								+ groupName + "!");
				}
				return;
			}
		});
		GridBagConstraints gbc_btnAddUser = new GridBagConstraints();
		gbc_btnAddUser.anchor = GridBagConstraints.WEST;
		gbc_btnAddUser.insets = new Insets(0, 0, 5, 0);
		gbc_btnAddUser.gridx = 0;
		gbc_btnAddUser.gridy = 3;
		gbc_btnAddUser.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(btnAddUser, gbc_btnAddUser);

		// Remove User button
		JButton btnRemoveUser = new JButton("Remove User");
		btnRemoveUser.setActionCommand("Remove User");
		btnRemoveUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int memberIndex = memberList.getSelectedIndex();
				int groupIndex = groupList.getSelectedIndex();
				if ((memberIndex == -1) || (groupIndex == -1)) {
					JOptionPane.showMessageDialog(contentPane, "Select a group and a user");
				} else {
					String removeUserName = memberListModel.get(memberIndex).toString();
					String selectedGroup = groupListModel.get(groupIndex).toString();
					if (!removeUserName.equals(userToken.getSubject())) {
						int reply = JOptionPane.showConfirmDialog(contentPane, "Really remove user: " + removeUserName,
								"WARNING", JOptionPane.YES_NO_OPTION);
						if (reply == JOptionPane.YES_OPTION) {
							if (gclient.deleteUserFromGroup(removeUserName, selectedGroup, userToken))
								memberListModel.remove(memberIndex);
							else
								JOptionPane.showMessageDialog(contentPane, "Failed to remove: " + removeUserName
										+ " from " + selectedGroup + "!");
						}
					} else
						JOptionPane.showMessageDialog(contentPane, "You can't delete yourself you idiot");
				}
				return;
			}
		});
		GridBagConstraints gbc_btnRemoveUser = new GridBagConstraints();
		gbc_btnRemoveUser.anchor = GridBagConstraints.WEST;
		gbc_btnRemoveUser.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveUser.gridx = 0;
		gbc_btnRemoveUser.gridy = 4;
		gbc_btnRemoveUser.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(btnRemoveUser, gbc_btnRemoveUser);

		// Create User button
		JButton btnCreateUser = new JButton("Create User");
		btnCreateUser.setActionCommand("Create User");
		btnCreateUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newUserName = RunClient.askForValidInput("Enter user's name to create", contentPane);
				if (newUserName != null) {
					if (gclient.createUser(newUserName, userToken))
						JOptionPane.showMessageDialog(contentPane, "User: " + newUserName + " successfully created");
					else
						JOptionPane.showMessageDialog(contentPane, "Failed to create user: " + newUserName + "!");
				}
				return;
			}
		});
		GridBagConstraints gbc_btnCreateUser = new GridBagConstraints();
		gbc_btnCreateUser.anchor = GridBagConstraints.WEST;
		gbc_btnCreateUser.insets = new Insets(0, 0, 5, 0);
		gbc_btnCreateUser.gridx = 0;
		gbc_btnCreateUser.gridy = 5;
		gbc_btnCreateUser.fill = GridBagConstraints.HORIZONTAL;
		if (!userToken.getGroups().contains("ADMIN"))
			btnCreateUser.setEnabled(false);
		leftPanel.add(btnCreateUser, gbc_btnCreateUser);

		// Delete User button
		JButton btnDeleteUser = new JButton("Delete User");
		btnDeleteUser.setActionCommand("Delete User");
		btnDeleteUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String deleteUserName = RunClient.askForValidInput("Enter user's name to delete", contentPane);
				if (deleteUserName != null) {
					if (!deleteUserName.equals(userToken.getSubject())) {
						System.out.println("DELETE MYSELF WTF: " + deleteUserName + " : " + userToken.getSubject());
						int reply = JOptionPane.showConfirmDialog(contentPane, "Really delete user: " + deleteUserName,
								"WARNING", JOptionPane.YES_NO_OPTION);
						if (reply == JOptionPane.YES_OPTION) {
							if (gclient.deleteUser(deleteUserName, userToken))
								JOptionPane.showMessageDialog(contentPane, "User: " + deleteUserName
										+ " successfully deleted");
							else
								JOptionPane.showMessageDialog(contentPane, "Failed to delete user: " + deleteUserName
										+ "!");
						}
					} else
						JOptionPane.showMessageDialog(contentPane, "You can't delete yourself you idiot");
				}
				return;
			}
		});
		GridBagConstraints gbc_btnDeleteUser = new GridBagConstraints();
		gbc_btnDeleteUser.anchor = GridBagConstraints.WEST;
		gbc_btnDeleteUser.insets = new Insets(0, 0, 5, 0);
		gbc_btnDeleteUser.gridx = 0;
		gbc_btnDeleteUser.gridy = 6;
		gbc_btnDeleteUser.fill = GridBagConstraints.HORIZONTAL;
		if (!userToken.getGroups().contains("ADMIN"))
			btnDeleteUser.setEnabled(false);
		leftPanel.add(btnDeleteUser, gbc_btnDeleteUser);

		// List Users button
		JButton btnListUser = new JButton("List Users");
		btnListUser.setActionCommand("List Users");
		btnListUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int groupIndex = groupList.getSelectedIndex();
				if (groupIndex != -1) {
					String selectedGroup = groupListModel.get(groupIndex).toString();
					if (!selectedGroup.equals(lastListed)) {
						lastListed = selectedGroup;
						List<String> members = gclient.listMembers(selectedGroup, userToken);
						if (members == null)
							JOptionPane.showMessageDialog(contentPane, "Failed to retreive member list for group: "
									+ selectedGroup);
						else {
							memberListModel.removeAllElements();
							for (String member : members) {
								memberListModel.addElement(member);
							}
						}
					}
				}
				return;
			}
		});
		GridBagConstraints gbc_btnListUser = new GridBagConstraints();
		gbc_btnListUser.anchor = GridBagConstraints.WEST;
		gbc_btnListUser.insets = new Insets(0, 0, 5, 0);
		gbc_btnListUser.gridx = 0;
		gbc_btnListUser.gridy = 7;
		gbc_btnListUser.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(btnListUser, gbc_btnListUser);

		// Group list panel: label + list
		JPanel groupPanelContainer = new JPanel();
		groupPanelContainer.add(new JLabel("My Groups"));
		groupPanelContainer.add(groupListScrollPane);
		groupPanelContainer.setLayout(new BoxLayout(groupPanelContainer, BoxLayout.Y_AXIS));

		// Members list panel: label + list
		JPanel membersPanelContainer = new JPanel();
		membersPanelContainer.add(new JLabel("Members"));
		membersPanelContainer.add(memberListScrollPane);
		membersPanelContainer.setLayout(new BoxLayout(membersPanelContainer, BoxLayout.Y_AXIS));

		// Bring everything together
		JSplitPane rightPanel = new JSplitPane();
		rightPanel.setLeftComponent(groupPanelContainer);
		rightPanel.setRightComponent(membersPanelContainer);
		contentPane.setRightComponent(rightPanel);
	}
}
