import { defineStore } from "pinia";
import { decodeToken } from "@/libs/jwtToken";

export const useCartStore = defineStore("cart", {
  state: () => ({
    items: JSON.parse(localStorage.getItem("cart_items_v1") || "[]"),
  }),
  getters: {
    totalItems: (state) => state.items.reduce((s, i) => s + i.quantity, 0),
    totalPrice: (state) => state.items.reduce((s, i) => s + i.quantity * i.price, 0),
    groupedBySeller: (state) => {
      const map = new Map();
      for (const it of state.items) {
        if (!map.has(it.sellerId)) {
          map.set(it.sellerId, {
            sellerId: it.sellerId,
            sellerNickname: it.sellerNickname || "Seller",
            items: [],
            sellerTotal: 0,
          });
        }
        const g = map.get(it.sellerId);
        g.items.push(it);
        g.sellerTotal += it.price * it.quantity;
      }
      return Array.from(map.values());
    },
  },
  actions: {
    _save() {
      localStorage.setItem("cart_items_v1", JSON.stringify(this.items));
    },

    addToCart(item, seller, qty = 1) {
      if (!item || !item.itemId) return;

      let currentUserId = null;
      const token = localStorage.getItem("accessToken");
      if (token) {
        const decoded = decodeToken(token);
        currentUserId = decoded?.buyerId || decoded?.id || decoded?.sub || null;
      }

      const sellerId =
        seller?.sellerId ??
        item.sellerId ??
        (item.seller && (item.seller.id ?? item.seller._id)) ??
        "unknown";

      if (currentUserId && String(currentUserId) === String(sellerId)) {
        return;
      }

      const sellerNickname =
        seller?.sellerNickname ??
        item.sellerNickname ??
        item.sellerName ??
        (item.seller && (item.seller.nickname ?? item.seller.name)) ??
        "Seller";

      const resolveImageFromArray = (arr) => {
        if (!Array.isArray(arr) || !arr.length) return "";
        const first = arr.find(Boolean);
        if (!first) return "";
        if (typeof first === "string") return first;
        return first.url ?? first.src ?? first.imageUrl ?? first.thumbnail ?? first.thumb ?? "";
      };

      const image =
        item.image ??
        item.imageUrl ??
        item.image_url ??
        item.thumbnail ??
        item.thumb ??
        item.img ??
        item.picture ??
        resolveImageFromArray(item.images) ??
        resolveImageFromArray(item.pictures) ??
        (item.media && item.media[0] && (item.media[0].url ?? item.media[0].src)) ??
        "";

      const avail = Number(item.availableStock ?? item.stock ?? item.qty ?? 9999);

      const existing = this.items.find(
        (i) => i.itemId === item.itemId && String(i.sellerId) === String(sellerId)
      );

      if (existing) {
        existing.quantity = Math.min(existing.quantity + qty, avail);
        if (image) existing.image = image;
        if (!existing.sellerNickname || existing.sellerNickname === "Seller") {
          existing.sellerNickname = sellerNickname;
        }
        existing.price = Number(item.price ?? existing.price ?? 0);
        existing.name = item.name ?? item.title ?? existing.name;
        existing.availableStock = avail;
        existing.brand = item.brand ?? existing.brand ?? "Brand";
        existing.color = item.color ?? existing.color ?? null;
        existing.storageGb = item.storageGb ?? existing.storageGb ?? null;
      } else {
        this.items.push({
          sellerId,
          sellerNickname,
          itemId: item.itemId,
          name: item.name ?? item.title ?? "",
          brand: item.brand ?? "Brand",
          color: item.color ?? null,
          storageGb: item.storageGb ?? null,
          price: Number(item.price ?? 0),
          quantity: Math.min(qty, avail),
          availableStock: avail,
          image,
        });
      }

      this._save();
    },

    increment(item) {
      const it = this.items.find(
        (i) => i.itemId === item.itemId && String(i.sellerId) === String(item.sellerId)
      );
      if (!it) return;
      if (it.quantity < (it.availableStock ?? 9999)) {
        it.quantity++;
        this._save();
      }
    },

    decrement(item) {
      const it = this.items.find(
        (i) => i.itemId === item.itemId && String(i.sellerId) === String(item.sellerId)
      );
      if (!it) return;
      if (it.quantity > 1) {
        it.quantity--;
        this._save();
      }
    },

    updateQuantity(item, newQty) {
      const it = this.items.find(
        (i) => i.itemId === item.itemId && String(i.sellerId) === String(item.sellerId)
      );
      if (!it) return;
      if (newQty <= 0) {
        this.removeItem(it);
        return;
      }
      it.quantity = Math.min(newQty, it.availableStock ?? 9999);
      this._save();
    },

    removeItem(item) {
      this.items = this.items.filter(
        (i) => !(i.itemId === item.itemId && String(i.sellerId) === String(item.sellerId))
      );
      this._save();
    },

    clearCart() {
      this.items = [];
      this._save();
    },
  },
});
