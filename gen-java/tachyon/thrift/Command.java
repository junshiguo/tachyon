/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package tachyon.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-03-23")
public class Command implements org.apache.thrift.TBase<Command, Command._Fields>, java.io.Serializable, Cloneable, Comparable<Command> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Command");

  private static final org.apache.thrift.protocol.TField M_COMMAND_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("mCommandType", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField M_DATA_FIELD_DESC = new org.apache.thrift.protocol.TField("mData", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField M_BLOCK_INFO_FIELD_DESC = new org.apache.thrift.protocol.TField("mBlockInfo", org.apache.thrift.protocol.TType.LIST, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new CommandStandardSchemeFactory());
    schemes.put(TupleScheme.class, new CommandTupleSchemeFactory());
  }

  /**
   * 
   * @see CommandType
   */
  public CommandType mCommandType; // required
  public List<Long> mData; // required
  public List<ClientBlockInfo> mBlockInfo; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see CommandType
     */
    M_COMMAND_TYPE((short)1, "mCommandType"),
    M_DATA((short)2, "mData"),
    M_BLOCK_INFO((short)3, "mBlockInfo");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // M_COMMAND_TYPE
          return M_COMMAND_TYPE;
        case 2: // M_DATA
          return M_DATA;
        case 3: // M_BLOCK_INFO
          return M_BLOCK_INFO;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.M_COMMAND_TYPE, new org.apache.thrift.meta_data.FieldMetaData("mCommandType", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, CommandType.class)));
    tmpMap.put(_Fields.M_DATA, new org.apache.thrift.meta_data.FieldMetaData("mData", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64))));
    tmpMap.put(_Fields.M_BLOCK_INFO, new org.apache.thrift.meta_data.FieldMetaData("mBlockInfo", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ClientBlockInfo.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Command.class, metaDataMap);
  }

  public Command() {
  }

  public Command(
    CommandType mCommandType,
    List<Long> mData,
    List<ClientBlockInfo> mBlockInfo)
  {
    this();
    this.mCommandType = mCommandType;
    this.mData = mData;
    this.mBlockInfo = mBlockInfo;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Command(Command other) {
    if (other.isSetMCommandType()) {
      this.mCommandType = other.mCommandType;
    }
    if (other.isSetMData()) {
      List<Long> __this__mData = new ArrayList<Long>(other.mData);
      this.mData = __this__mData;
    }
    if (other.isSetMBlockInfo()) {
      List<ClientBlockInfo> __this__mBlockInfo = new ArrayList<ClientBlockInfo>(other.mBlockInfo.size());
      for (ClientBlockInfo other_element : other.mBlockInfo) {
        __this__mBlockInfo.add(new ClientBlockInfo(other_element));
      }
      this.mBlockInfo = __this__mBlockInfo;
    }
  }

  public Command deepCopy() {
    return new Command(this);
  }

  @Override
  public void clear() {
    this.mCommandType = null;
    this.mData = null;
    this.mBlockInfo = null;
  }

  /**
   * 
   * @see CommandType
   */
  public CommandType getMCommandType() {
    return this.mCommandType;
  }

  /**
   * 
   * @see CommandType
   */
  public Command setMCommandType(CommandType mCommandType) {
    this.mCommandType = mCommandType;
    return this;
  }

  public void unsetMCommandType() {
    this.mCommandType = null;
  }

  /** Returns true if field mCommandType is set (has been assigned a value) and false otherwise */
  public boolean isSetMCommandType() {
    return this.mCommandType != null;
  }

  public void setMCommandTypeIsSet(boolean value) {
    if (!value) {
      this.mCommandType = null;
    }
  }

  public int getMDataSize() {
    return (this.mData == null) ? 0 : this.mData.size();
  }

  public java.util.Iterator<Long> getMDataIterator() {
    return (this.mData == null) ? null : this.mData.iterator();
  }

  public void addToMData(long elem) {
    if (this.mData == null) {
      this.mData = new ArrayList<Long>();
    }
    this.mData.add(elem);
  }

  public List<Long> getMData() {
    return this.mData;
  }

  public Command setMData(List<Long> mData) {
    this.mData = mData;
    return this;
  }

  public void unsetMData() {
    this.mData = null;
  }

  /** Returns true if field mData is set (has been assigned a value) and false otherwise */
  public boolean isSetMData() {
    return this.mData != null;
  }

  public void setMDataIsSet(boolean value) {
    if (!value) {
      this.mData = null;
    }
  }

  public int getMBlockInfoSize() {
    return (this.mBlockInfo == null) ? 0 : this.mBlockInfo.size();
  }

  public java.util.Iterator<ClientBlockInfo> getMBlockInfoIterator() {
    return (this.mBlockInfo == null) ? null : this.mBlockInfo.iterator();
  }

  public void addToMBlockInfo(ClientBlockInfo elem) {
    if (this.mBlockInfo == null) {
      this.mBlockInfo = new ArrayList<ClientBlockInfo>();
    }
    this.mBlockInfo.add(elem);
  }

  public List<ClientBlockInfo> getMBlockInfo() {
    return this.mBlockInfo;
  }

  public Command setMBlockInfo(List<ClientBlockInfo> mBlockInfo) {
    this.mBlockInfo = mBlockInfo;
    return this;
  }

  public void unsetMBlockInfo() {
    this.mBlockInfo = null;
  }

  /** Returns true if field mBlockInfo is set (has been assigned a value) and false otherwise */
  public boolean isSetMBlockInfo() {
    return this.mBlockInfo != null;
  }

  public void setMBlockInfoIsSet(boolean value) {
    if (!value) {
      this.mBlockInfo = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case M_COMMAND_TYPE:
      if (value == null) {
        unsetMCommandType();
      } else {
        setMCommandType((CommandType)value);
      }
      break;

    case M_DATA:
      if (value == null) {
        unsetMData();
      } else {
        setMData((List<Long>)value);
      }
      break;

    case M_BLOCK_INFO:
      if (value == null) {
        unsetMBlockInfo();
      } else {
        setMBlockInfo((List<ClientBlockInfo>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case M_COMMAND_TYPE:
      return getMCommandType();

    case M_DATA:
      return getMData();

    case M_BLOCK_INFO:
      return getMBlockInfo();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case M_COMMAND_TYPE:
      return isSetMCommandType();
    case M_DATA:
      return isSetMData();
    case M_BLOCK_INFO:
      return isSetMBlockInfo();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Command)
      return this.equals((Command)that);
    return false;
  }

  public boolean equals(Command that) {
    if (that == null)
      return false;

    boolean this_present_mCommandType = true && this.isSetMCommandType();
    boolean that_present_mCommandType = true && that.isSetMCommandType();
    if (this_present_mCommandType || that_present_mCommandType) {
      if (!(this_present_mCommandType && that_present_mCommandType))
        return false;
      if (!this.mCommandType.equals(that.mCommandType))
        return false;
    }

    boolean this_present_mData = true && this.isSetMData();
    boolean that_present_mData = true && that.isSetMData();
    if (this_present_mData || that_present_mData) {
      if (!(this_present_mData && that_present_mData))
        return false;
      if (!this.mData.equals(that.mData))
        return false;
    }

    boolean this_present_mBlockInfo = true && this.isSetMBlockInfo();
    boolean that_present_mBlockInfo = true && that.isSetMBlockInfo();
    if (this_present_mBlockInfo || that_present_mBlockInfo) {
      if (!(this_present_mBlockInfo && that_present_mBlockInfo))
        return false;
      if (!this.mBlockInfo.equals(that.mBlockInfo))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_mCommandType = true && (isSetMCommandType());
    list.add(present_mCommandType);
    if (present_mCommandType)
      list.add(mCommandType.getValue());

    boolean present_mData = true && (isSetMData());
    list.add(present_mData);
    if (present_mData)
      list.add(mData);

    boolean present_mBlockInfo = true && (isSetMBlockInfo());
    list.add(present_mBlockInfo);
    if (present_mBlockInfo)
      list.add(mBlockInfo);

    return list.hashCode();
  }

  @Override
  public int compareTo(Command other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetMCommandType()).compareTo(other.isSetMCommandType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMCommandType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mCommandType, other.mCommandType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMData()).compareTo(other.isSetMData());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMData()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mData, other.mData);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMBlockInfo()).compareTo(other.isSetMBlockInfo());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMBlockInfo()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mBlockInfo, other.mBlockInfo);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Command(");
    boolean first = true;

    sb.append("mCommandType:");
    if (this.mCommandType == null) {
      sb.append("null");
    } else {
      sb.append(this.mCommandType);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("mData:");
    if (this.mData == null) {
      sb.append("null");
    } else {
      sb.append(this.mData);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("mBlockInfo:");
    if (this.mBlockInfo == null) {
      sb.append("null");
    } else {
      sb.append(this.mBlockInfo);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class CommandStandardSchemeFactory implements SchemeFactory {
    public CommandStandardScheme getScheme() {
      return new CommandStandardScheme();
    }
  }

  private static class CommandStandardScheme extends StandardScheme<Command> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Command struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // M_COMMAND_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.mCommandType = tachyon.thrift.CommandType.findByValue(iprot.readI32());
              struct.setMCommandTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // M_DATA
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list40 = iprot.readListBegin();
                struct.mData = new ArrayList<Long>(_list40.size);
                long _elem41;
                for (int _i42 = 0; _i42 < _list40.size; ++_i42)
                {
                  _elem41 = iprot.readI64();
                  struct.mData.add(_elem41);
                }
                iprot.readListEnd();
              }
              struct.setMDataIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // M_BLOCK_INFO
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list43 = iprot.readListBegin();
                struct.mBlockInfo = new ArrayList<ClientBlockInfo>(_list43.size);
                ClientBlockInfo _elem44;
                for (int _i45 = 0; _i45 < _list43.size; ++_i45)
                {
                  _elem44 = new ClientBlockInfo();
                  _elem44.read(iprot);
                  struct.mBlockInfo.add(_elem44);
                }
                iprot.readListEnd();
              }
              struct.setMBlockInfoIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Command struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.mCommandType != null) {
        oprot.writeFieldBegin(M_COMMAND_TYPE_FIELD_DESC);
        oprot.writeI32(struct.mCommandType.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.mData != null) {
        oprot.writeFieldBegin(M_DATA_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I64, struct.mData.size()));
          for (long _iter46 : struct.mData)
          {
            oprot.writeI64(_iter46);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.mBlockInfo != null) {
        oprot.writeFieldBegin(M_BLOCK_INFO_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.mBlockInfo.size()));
          for (ClientBlockInfo _iter47 : struct.mBlockInfo)
          {
            _iter47.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CommandTupleSchemeFactory implements SchemeFactory {
    public CommandTupleScheme getScheme() {
      return new CommandTupleScheme();
    }
  }

  private static class CommandTupleScheme extends TupleScheme<Command> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Command struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetMCommandType()) {
        optionals.set(0);
      }
      if (struct.isSetMData()) {
        optionals.set(1);
      }
      if (struct.isSetMBlockInfo()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetMCommandType()) {
        oprot.writeI32(struct.mCommandType.getValue());
      }
      if (struct.isSetMData()) {
        {
          oprot.writeI32(struct.mData.size());
          for (long _iter48 : struct.mData)
          {
            oprot.writeI64(_iter48);
          }
        }
      }
      if (struct.isSetMBlockInfo()) {
        {
          oprot.writeI32(struct.mBlockInfo.size());
          for (ClientBlockInfo _iter49 : struct.mBlockInfo)
          {
            _iter49.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Command struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.mCommandType = tachyon.thrift.CommandType.findByValue(iprot.readI32());
        struct.setMCommandTypeIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list50 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I64, iprot.readI32());
          struct.mData = new ArrayList<Long>(_list50.size);
          long _elem51;
          for (int _i52 = 0; _i52 < _list50.size; ++_i52)
          {
            _elem51 = iprot.readI64();
            struct.mData.add(_elem51);
          }
        }
        struct.setMDataIsSet(true);
      }
      if (incoming.get(2)) {
        {
          org.apache.thrift.protocol.TList _list53 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.mBlockInfo = new ArrayList<ClientBlockInfo>(_list53.size);
          ClientBlockInfo _elem54;
          for (int _i55 = 0; _i55 < _list53.size; ++_i55)
          {
            _elem54 = new ClientBlockInfo();
            _elem54.read(iprot);
            struct.mBlockInfo.add(_elem54);
          }
        }
        struct.setMBlockInfoIsSet(true);
      }
    }
  }

}
