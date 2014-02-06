import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GroupPanel(final GroupClient gclient, final UserToken userToken) {
		this.gclient = gclient;
		this.userToken = userToken;
		
		// Main pane. Buttons on left, lists on right.
		final JSplitPane contentPane = new JSplitPane();
		add(contentPane);
		
		// Setup left panel
		JPanel leftPanel = new JPanel();
		contentPane.setLeftComponent(leftPanel);
		GridBagLayout gbl_leftPanel = new GridBagLayout();
		gbl_leftPanel.columnWidths = new int[]{130, 0};
		gbl_leftPanel.rowHeights = new int[]{15, 25, 25, 25, 25, 0};
		gbl_leftPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_leftPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
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
		memberListModel.addElement("user1"); // TODO populate user list from group server;
		final JList memberList = new JList(memberListModel);
		memberList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane memberListScrollPane = new JScrollPane(memberList);
		
		// Add Group button
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
				String newGroupName = JOptionPane.showInputDialog("Enter new group name");
				if(groupListModel.contains(newGroupName)) {
					JOptionPane.showMessageDialog(contentPane, "Group already exists!");
					return;
				}
				if(!gclient.createGroup(newGroupName, userToken))
					JOptionPane.showMessageDialog(contentPane, "Failed to create group!");
				else
					groupListModel.insertElementAt(newGroupName, groupListModel.size());
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
				if(index != -1) {
					if(gclient.deleteGroup(groupListModel.get(index).toString(), userToken))
						groupListModel.remove(index);
					else
						JOptionPane.showMessageDialog(contentPane, "Failed to delete group!");
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
				if(index == -1) {
					JOptionPane.showMessageDialog(contentPane, "Select a group");
					return;
				}
				String newUserName = JOptionPane.showInputDialog("Enter user's name");
				if(memberListModel.contains(newUserName)) {
					JOptionPane.showMessageDialog(contentPane, "User already belongs to group!");
					return;
				}
				if(gclient.addUserToGroup(newUserName, groupListModel.get(index).toString(), userToken))
					memberListModel.insertElementAt(newUserName, memberListModel.size());
				else
					JOptionPane.showMessageDialog(contentPane, "Failed to add user!");
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
		
		// Delete User button
		JButton btnDeleteUser = new JButton("Delete User");
		btnDeleteUser.setActionCommand("Delete User");
		btnDeleteUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int memberIndex = memberList.getSelectedIndex();
				int groupIndex = groupList.getSelectedIndex();
				if((memberIndex != -1) && (groupIndex != -1)) {
					if(gclient.deleteUserFromGroup(memberListModel.get(memberIndex).toString(), groupListModel.get(groupIndex).toString(), userToken))
						memberListModel.remove(memberIndex);
					else
						JOptionPane.showMessageDialog(contentPane, "Failed to delete user from group!");
				}
				return;
			}
		});
		GridBagConstraints gbc_btnDeleteUser = new GridBagConstraints();
		gbc_btnDeleteUser.anchor = GridBagConstraints.WEST;
		gbc_btnDeleteUser.gridx = 0;
		gbc_btnDeleteUser.gridy = 4;
		gbc_btnDeleteUser.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(btnDeleteUser, gbc_btnDeleteUser);
		
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

