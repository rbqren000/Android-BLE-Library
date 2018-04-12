package no.nordicsemi.android.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import no.nordicsemi.android.ble.callback.DefaultMtuSpitter;
import no.nordicsemi.android.ble.callback.FailCallback;
import no.nordicsemi.android.ble.callback.SuccessCallback;
import no.nordicsemi.android.ble.callback.ValueSplitter;

public class WriteRequest extends Request {
	private final static ValueSplitter MTU_SPLITTER = new DefaultMtuSpitter();

	private ValueSplitter valueSplitter;
	private final byte[] data;
	private final int writeType;
	private int count = 0;

	WriteRequest(final @NonNull Type type, final @NonNull BluetoothGattCharacteristic characteristic,
				 final @NonNull byte[] data, final int offset, final int length, final int writeType) {
		super(type, characteristic);
		this.data = copy(data, offset, length);
		this.writeType = writeType;
	}

	WriteRequest(final @NonNull Type type, final @NonNull BluetoothGattDescriptor descriptor,
				 final @NonNull byte[] data, final int offset, final int length) {
		super(type, descriptor);
		this.data = copy(data, offset, length);
		this.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
	}

	private static byte[] copy(final @NonNull byte[] value, final int offset, final int length) {
		if (value == null || offset > value.length)
			return null;
		final int maxLength = Math.min(value.length - offset, length);
		final byte[] copy = new byte[maxLength];
		System.arraycopy(value, offset, copy, 0, maxLength);
		return copy;
	}

	@NonNull
	public WriteRequest split(final @NonNull ValueSplitter splitter) {
		this.valueSplitter = splitter;
		return this;
	}

	@NonNull
	public WriteRequest split() {
		this.valueSplitter = MTU_SPLITTER;
		return this;
	}

	@Override
	@NonNull
	public WriteRequest done(final @NonNull SuccessCallback callback) {
		super.done(callback);
		return this;
	}

	@Override
	@NonNull
	public WriteRequest fail(final @NonNull FailCallback callback) {
		super.fail(callback);
		return this;
	}

	byte[] getData(final int mtu) {
		if (valueSplitter == null)
			return data;

		final byte[] chunk = valueSplitter.chunk(data, count++, mtu - 3);
		if (chunk == null) // all data were sent
			count = 0;
		return chunk;
	}

	boolean hasMore() {
		return count > 0;
	}

	int getWriteType() {
		return writeType;
	}
}
