package com.common.tool.util.permission

/**
 * @author ���ۺ�
 *
 * @features Ȩ�޽��
 */
sealed class PermissionResult {
    /**
     * ����Ȩ��
     */
    object Grant : PermissionResult()

    /**
     * �ܾ�Ȩ�ޣ�����ѯ��
     */
    class Deny(val permissions: Array<String>) : PermissionResult()

    /**
     * ������Ȩ��
     */
    class NoGrant(val permissions: Array<String>) : PermissionResult()

    /**
     * Ȩ�޼��
     */
    class PermissionCheck(val permissions: Array<String>) : PermissionResult()


}