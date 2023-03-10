package com.happy.otc.proto;// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: appealInfo.proto

public final class AppealInfo {
  private AppealInfo() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  /**
   * Protobuf enum {@code AppealType}
   */
  public enum AppealType
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <pre>
     *无
     * </pre>
     *
     * <code>NULL = 0;</code>
     */
    NULL(0),
    /**
     * <pre>
     *对方未付款
     * </pre>
     *
     * <code>NO_PAY = 1;</code>
     */
    NO_PAY(1),
    /**
     * <pre>
     *对方未放行
     * </pre>
     *
     * <code>NO_PASS = 2;</code>
     */
    NO_PASS(2),
    /**
     * <pre>
     *对方无应答
     * </pre>
     *
     * <code>NO_REPLY = 3;</code>
     */
    NO_REPLY(3),
    /**
     * <pre>
     *对方有欺诈行为
     * </pre>
     *
     * <code>FAKE = 4;</code>
     */
    FAKE(4),
    /**
     * <pre>
     *其他
     * </pre>
     *
     * <code>OTHER = 5;</code>
     */
    OTHER(5),
    UNRECOGNIZED(-1),
    ;

    /**
     * <pre>
     *无
     * </pre>
     *
     * <code>NULL = 0;</code>
     */
    public static final int NULL_VALUE = 0;
    /**
     * <pre>
     *对方未付款
     * </pre>
     *
     * <code>NO_PAY = 1;</code>
     */
    public static final int NO_PAY_VALUE = 1;
    /**
     * <pre>
     *对方未放行
     * </pre>
     *
     * <code>NO_PASS = 2;</code>
     */
    public static final int NO_PASS_VALUE = 2;
    /**
     * <pre>
     *对方无应答
     * </pre>
     *
     * <code>NO_REPLY = 3;</code>
     */
    public static final int NO_REPLY_VALUE = 3;
    /**
     * <pre>
     *对方有欺诈行为
     * </pre>
     *
     * <code>FAKE = 4;</code>
     */
    public static final int FAKE_VALUE = 4;
    /**
     * <pre>
     *其他
     * </pre>
     *
     * <code>OTHER = 5;</code>
     */
    public static final int OTHER_VALUE = 5;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @Deprecated
    public static AppealType valueOf(int value) {
      return forNumber(value);
    }

    public static AppealType forNumber(int value) {
      switch (value) {
        case 0: return NULL;
        case 1: return NO_PAY;
        case 2: return NO_PASS;
        case 3: return NO_REPLY;
        case 4: return FAKE;
        case 5: return OTHER;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<AppealType>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        AppealType> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<AppealType>() {
            public AppealType findValueByNumber(int number) {
              return AppealType.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return AppealInfo.getDescriptor().getEnumTypes().get(0);
    }

    private static final AppealType[] VALUES = values();

    public static AppealType valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private AppealType(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:AppealType)
  }

  public interface AppealInfoVOOrBuilder extends
      // @@protoc_insertion_point(interface_extends:AppealInfoVO)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     *交易id
     * </pre>
     *
     * <code>int64 capitalDetailId = 1;</code>
     */
    long getCapitalDetailId();

    /**
     * <pre>
     *申诉类型
     * </pre>
     *
     * <code>.AppealType type = 2;</code>
     */
    int getTypeValue();
    /**
     * <pre>
     *申诉类型
     * </pre>
     *
     * <code>.AppealType type = 2;</code>
     */
    AppealInfo.AppealType getType();

    /**
     * <pre>
     *申诉理由
     * </pre>
     *
     * <code>string reason = 3;</code>
     */
    String getReason();
    /**
     * <pre>
     *申诉理由
     * </pre>
     *
     * <code>string reason = 3;</code>
     */
    com.google.protobuf.ByteString
        getReasonBytes();
  }
  /**
   * Protobuf type {@code AppealInfoVO}
   */
  public  static final class AppealInfoVO extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:AppealInfoVO)
      AppealInfoVOOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use AppealInfoVO.newBuilder() to construct.
    private AppealInfoVO(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private AppealInfoVO() {
      capitalDetailId_ = 0L;
      type_ = 0;
      reason_ = "";
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private AppealInfoVO(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownFieldProto3(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {

              capitalDetailId_ = input.readInt64();
              break;
            }
            case 16: {
              int rawValue = input.readEnum();

              type_ = rawValue;
              break;
            }
            case 26: {
              String s = input.readStringRequireUtf8();

              reason_ = s;
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return AppealInfo.internal_static_AppealInfoVO_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return AppealInfo.internal_static_AppealInfoVO_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              AppealInfo.AppealInfoVO.class, AppealInfo.AppealInfoVO.Builder.class);
    }

    public static final int CAPITALDETAILID_FIELD_NUMBER = 1;
    private long capitalDetailId_;
    /**
     * <pre>
     *交易id
     * </pre>
     *
     * <code>int64 capitalDetailId = 1;</code>
     */
    public long getCapitalDetailId() {
      return capitalDetailId_;
    }

    public static final int TYPE_FIELD_NUMBER = 2;
    private int type_;
    /**
     * <pre>
     *申诉类型
     * </pre>
     *
     * <code>.AppealType type = 2;</code>
     */
    public int getTypeValue() {
      return type_;
    }
    /**
     * <pre>
     *申诉类型
     * </pre>
     *
     * <code>.AppealType type = 2;</code>
     */
    public AppealInfo.AppealType getType() {
      AppealInfo.AppealType result = AppealInfo.AppealType.valueOf(type_);
      return result == null ? AppealInfo.AppealType.UNRECOGNIZED : result;
    }

    public static final int REASON_FIELD_NUMBER = 3;
    private volatile Object reason_;
    /**
     * <pre>
     *申诉理由
     * </pre>
     *
     * <code>string reason = 3;</code>
     */
    public String getReason() {
      Object ref = reason_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        reason_ = s;
        return s;
      }
    }
    /**
     * <pre>
     *申诉理由
     * </pre>
     *
     * <code>string reason = 3;</code>
     */
    public com.google.protobuf.ByteString
        getReasonBytes() {
      Object ref = reason_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        reason_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (capitalDetailId_ != 0L) {
        output.writeInt64(1, capitalDetailId_);
      }
      if (type_ != AppealInfo.AppealType.NULL.getNumber()) {
        output.writeEnum(2, type_);
      }
      if (!getReasonBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, reason_);
      }
      unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (capitalDetailId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(1, capitalDetailId_);
      }
      if (type_ != AppealInfo.AppealType.NULL.getNumber()) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(2, type_);
      }
      if (!getReasonBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, reason_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof AppealInfo.AppealInfoVO)) {
        return super.equals(obj);
      }
      AppealInfo.AppealInfoVO other = (AppealInfo.AppealInfoVO) obj;

      boolean result = true;
      result = result && (getCapitalDetailId()
          == other.getCapitalDetailId());
      result = result && type_ == other.type_;
      result = result && getReason()
          .equals(other.getReason());
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + CAPITALDETAILID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getCapitalDetailId());
      hash = (37 * hash) + TYPE_FIELD_NUMBER;
      hash = (53 * hash) + type_;
      hash = (37 * hash) + REASON_FIELD_NUMBER;
      hash = (53 * hash) + getReason().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static AppealInfo.AppealInfoVO parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static AppealInfo.AppealInfoVO parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static AppealInfo.AppealInfoVO parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static AppealInfo.AppealInfoVO parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static AppealInfo.AppealInfoVO parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static AppealInfo.AppealInfoVO parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static AppealInfo.AppealInfoVO parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static AppealInfo.AppealInfoVO parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static AppealInfo.AppealInfoVO parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static AppealInfo.AppealInfoVO parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static AppealInfo.AppealInfoVO parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static AppealInfo.AppealInfoVO parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(AppealInfo.AppealInfoVO prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code AppealInfoVO}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:AppealInfoVO)
        AppealInfo.AppealInfoVOOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return AppealInfo.internal_static_AppealInfoVO_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return AppealInfo.internal_static_AppealInfoVO_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                AppealInfo.AppealInfoVO.class, AppealInfo.AppealInfoVO.Builder.class);
      }

      // Construct using AppealInfo.AppealInfoVO.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        capitalDetailId_ = 0L;

        type_ = 0;

        reason_ = "";

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return AppealInfo.internal_static_AppealInfoVO_descriptor;
      }

      public AppealInfo.AppealInfoVO getDefaultInstanceForType() {
        return AppealInfo.AppealInfoVO.getDefaultInstance();
      }

      public AppealInfo.AppealInfoVO build() {
        AppealInfo.AppealInfoVO result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public AppealInfo.AppealInfoVO buildPartial() {
        AppealInfo.AppealInfoVO result = new AppealInfo.AppealInfoVO(this);
        result.capitalDetailId_ = capitalDetailId_;
        result.type_ = type_;
        result.reason_ = reason_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof AppealInfo.AppealInfoVO) {
          return mergeFrom((AppealInfo.AppealInfoVO)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(AppealInfo.AppealInfoVO other) {
        if (other == AppealInfo.AppealInfoVO.getDefaultInstance()) return this;
        if (other.getCapitalDetailId() != 0L) {
          setCapitalDetailId(other.getCapitalDetailId());
        }
        if (other.type_ != 0) {
          setTypeValue(other.getTypeValue());
        }
        if (!other.getReason().isEmpty()) {
          reason_ = other.reason_;
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        AppealInfo.AppealInfoVO parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (AppealInfo.AppealInfoVO) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private long capitalDetailId_ ;
      /**
       * <pre>
       *交易id
       * </pre>
       *
       * <code>int64 capitalDetailId = 1;</code>
       */
      public long getCapitalDetailId() {
        return capitalDetailId_;
      }
      /**
       * <pre>
       *交易id
       * </pre>
       *
       * <code>int64 capitalDetailId = 1;</code>
       */
      public Builder setCapitalDetailId(long value) {

        capitalDetailId_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *交易id
       * </pre>
       *
       * <code>int64 capitalDetailId = 1;</code>
       */
      public Builder clearCapitalDetailId() {

        capitalDetailId_ = 0L;
        onChanged();
        return this;
      }

      private int type_ = 0;
      /**
       * <pre>
       *申诉类型
       * </pre>
       *
       * <code>.AppealType type = 2;</code>
       */
      public int getTypeValue() {
        return type_;
      }
      /**
       * <pre>
       *申诉类型
       * </pre>
       *
       * <code>.AppealType type = 2;</code>
       */
      public Builder setTypeValue(int value) {
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *申诉类型
       * </pre>
       *
       * <code>.AppealType type = 2;</code>
       */
      public AppealInfo.AppealType getType() {
        AppealInfo.AppealType result = AppealInfo.AppealType.valueOf(type_);
        return result == null ? AppealInfo.AppealType.UNRECOGNIZED : result;
      }
      /**
       * <pre>
       *申诉类型
       * </pre>
       *
       * <code>.AppealType type = 2;</code>
       */
      public Builder setType(AppealInfo.AppealType value) {
        if (value == null) {
          throw new NullPointerException();
        }

        type_ = value.getNumber();
        onChanged();
        return this;
      }
      /**
       * <pre>
       *申诉类型
       * </pre>
       *
       * <code>.AppealType type = 2;</code>
       */
      public Builder clearType() {

        type_ = 0;
        onChanged();
        return this;
      }

      private Object reason_ = "";
      /**
       * <pre>
       *申诉理由
       * </pre>
       *
       * <code>string reason = 3;</code>
       */
      public String getReason() {
        Object ref = reason_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          reason_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <pre>
       *申诉理由
       * </pre>
       *
       * <code>string reason = 3;</code>
       */
      public com.google.protobuf.ByteString
          getReasonBytes() {
        Object ref = reason_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b =
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          reason_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       *申诉理由
       * </pre>
       *
       * <code>string reason = 3;</code>
       */
      public Builder setReason(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }

        reason_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *申诉理由
       * </pre>
       *
       * <code>string reason = 3;</code>
       */
      public Builder clearReason() {

        reason_ = getDefaultInstance().getReason();
        onChanged();
        return this;
      }
      /**
       * <pre>
       *申诉理由
       * </pre>
       *
       * <code>string reason = 3;</code>
       */
      public Builder setReasonBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

        reason_ = value;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFieldsProto3(unknownFields);
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:AppealInfoVO)
    }

    // @@protoc_insertion_point(class_scope:AppealInfoVO)
    private static final AppealInfo.AppealInfoVO DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new AppealInfo.AppealInfoVO();
    }

    public static AppealInfo.AppealInfoVO getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<AppealInfoVO>
        PARSER = new com.google.protobuf.AbstractParser<AppealInfoVO>() {
      public AppealInfoVO parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new AppealInfoVO(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<AppealInfoVO> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<AppealInfoVO> getParserForType() {
      return PARSER;
    }

    public AppealInfo.AppealInfoVO getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_AppealInfoVO_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_AppealInfoVO_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\020appealInfo.proto\"R\n\014AppealInfoVO\022\027\n\017ca" +
      "pitalDetailId\030\001 \001(\003\022\031\n\004type\030\002 \001(\0162\013.Appe" +
      "alType\022\016\n\006reason\030\003 \001(\t*R\n\nAppealType\022\010\n\004" +
      "NULL\020\000\022\n\n\006NO_PAY\020\001\022\013\n\007NO_PASS\020\002\022\014\n\010NO_RE" +
      "PLY\020\003\022\010\n\004FAKE\020\004\022\t\n\005OTHER\020\005b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_AppealInfoVO_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_AppealInfoVO_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_AppealInfoVO_descriptor,
        new String[] { "CapitalDetailId", "Type", "Reason", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
