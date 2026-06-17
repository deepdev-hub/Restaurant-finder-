import React from "react";
import { Link, useNavigate } from "react-router";
import { Plus, Star, MessageCircle, Edit, Store, ChevronRight, Eye, Trash2, AlertCircle } from "lucide-react";
import { getRestaurants, deleteRestaurant } from "../api/client";
import { useAuth } from "../context/AuthContext";
import { useApiData } from "../hooks/useApiData";
import { useLanguage } from "../context/LanguageContext";

export function OwnerRestaurantListPage() {
  const { currentUser, isLoggedIn } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();
  const { data: restaurants, setData: setRestaurants } = useApiData(
    () => currentUser ? getRestaurants(currentUser.id) : Promise.resolve([]),
    [currentUser?.id],
    []
  );

  const [restaurantToDelete, setRestaurantToDelete] = React.useState<{id: string, name: string} | null>(null);
  const [isDeleting, setIsDeleting] = React.useState(false);

  const handleDelete = async () => {
    if (!restaurantToDelete || !currentUser) return;
    try {
      setIsDeleting(true);
      await deleteRestaurant(restaurantToDelete.id, currentUser.id);
      setRestaurants(prev => prev.filter(r => r.id !== restaurantToDelete.id));
      setRestaurantToDelete(null);
      // Optional: show a toast success here
    } catch (error) {
      console.error("Failed to delete restaurant:", error);
      alert(t.ownerList.deleteError || "Có lỗi xảy ra khi xóa quán.");
    } finally {
      setIsDeleting(false);
    }
  };

  React.useEffect(() => {
    if (!isLoggedIn) navigate("/login");
  }, [isLoggedIn, navigate]);

  if (!isLoggedIn || !currentUser) return null;

  const displayRestaurants = restaurants;

  const formatPrice = (price: number) =>
    new Intl.NumberFormat("vi-VN").format(price) + "đ";

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-gray-900">{t.ownerList.title}</h1>
            <p className="text-sm text-gray-400 mt-0.5">{t.ownerList.titleSub}</p>
          </div>
          <Link
            to="/owner/register"
            className="flex items-center gap-2 px-4 py-2.5 text-sm text-white rounded-xl transition-all hover:opacity-90"
            style={{ background: "linear-gradient(135deg, #0066CC 0%, #004499 100%)" }}
          >
            <Plus className="w-4 h-4" />
            <span className="hidden sm:inline">{t.ownerList.addBtn}</span>
            <span className="sm:hidden">{t.ownerList.addBtnMobile}</span>
          </Link>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-4 mb-6">
          {[
            { label: t.ownerList.statStores, value: displayRestaurants.length, icon: "🏪" },
            { label: t.ownerList.statReviews, value: displayRestaurants.reduce((s, r) => s + r.reviewCount, 0), icon: "⭐" },
            {
              label: t.ownerList.statRating,
              value: displayRestaurants.length > 0
                ? (displayRestaurants.reduce((s, r) => s + r.rating, 0) / displayRestaurants.length).toFixed(1)
                : "–",
              icon: "📊",
            },
          ].map((stat) => (
            <div key={stat.label} className="bg-white rounded-2xl border border-gray-100 p-4 text-center">
              <div className="text-2xl mb-1">{stat.icon}</div>
              <div className="text-xl text-gray-900">{stat.value}</div>
              <div className="text-xs text-gray-400 mt-0.5">{stat.label}</div>
            </div>
          ))}
        </div>

        {/* Restaurant cards */}
        {displayRestaurants.length === 0 ? (
          <div className="text-center py-16 bg-white rounded-2xl border border-gray-100">
            <Store className="w-16 h-16 text-gray-200 mx-auto mb-4" />
            <h3 className="text-gray-700 mb-2">{t.ownerList.emptyTitle}</h3>
            <p className="text-sm text-gray-400 mb-6">{t.ownerList.emptyDesc}</p>
            <Link
              to="/owner/register"
              className="inline-flex items-center gap-2 px-6 py-3 text-sm text-white rounded-xl transition-all hover:opacity-90"
              style={{ background: "linear-gradient(135deg, #0066CC 0%, #004499 100%)" }}
            >
              <Plus className="w-4 h-4" />
              {t.ownerList.emptyBtn}
            </Link>
          </div>
        ) : (
          <div className="space-y-4">
            {displayRestaurants.map((restaurant) => (
              <div key={restaurant.id} className="bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-md transition-all">
                <div className="flex flex-col sm:flex-row">
                  <div className="w-full sm:w-40 h-32 sm:h-auto flex-shrink-0 overflow-hidden">
                    <img src={restaurant.coverImage} alt={restaurant.nameVn} className="w-full h-full object-cover" />
                  </div>
                  <div className="flex-1 p-5">
                    <div className="flex items-start justify-between gap-3 mb-3">
                      <div>
                        <h3 className="text-gray-900">{restaurant.nameJp}</h3>
                        <p className="text-sm text-gray-400 mt-0.5">{restaurant.nameVn}</p>
                      </div>
                      <span className={`text-xs px-2.5 py-1 rounded-full flex-shrink-0 ${
                        restaurant.status === "open" ? "bg-green-100 text-green-700" : "bg-red-100 text-red-600"
                      }`}>
                        {restaurant.status === "open" ? t.ownerList.open : t.ownerList.closed}
                      </span>
                    </div>

                    <div className="flex items-center gap-4 mb-4 text-sm text-gray-500">
                      <span className="flex items-center gap-1">
                        <Star className="w-4 h-4 fill-amber-400 text-amber-400" />
                        {restaurant.rating}
                      </span>
                      <span className="flex items-center gap-1">
                        <MessageCircle className="w-4 h-4 text-blue-400" />
                        {restaurant.reviewCount}{t.ownerList.reviewsCount}
                      </span>
                      <span className="text-blue-600">{formatPrice(restaurant.avgPrice)}〜</span>
                    </div>

                    <div className="flex flex-wrap items-center gap-2">
                      <Link
                        to={`/owner/manage/${restaurant.id}`}
                        className="flex items-center gap-1.5 px-3 py-2 text-sm text-blue-600 border border-blue-200 rounded-xl hover:bg-blue-50 transition-colors"
                      >
                        <Edit className="w-4 h-4" />
                        {t.ownerList.edit}
                      </Link>
                      <Link
                        to={`/restaurant/${restaurant.id}`}
                        className="flex items-center gap-1.5 px-3 py-2 text-sm text-gray-600 border border-gray-200 rounded-xl hover:bg-gray-50 transition-colors"
                      >
                        <Eye className="w-4 h-4" />
                        {t.ownerList.publicPage}
                      </Link>
                      <Link
                        to="/chat"
                        className="flex items-center gap-1.5 px-3 py-2 text-sm text-gray-600 border border-gray-200 rounded-xl hover:bg-gray-50 transition-colors"
                      >
                        <MessageCircle className="w-4 h-4" />
                        {t.ownerList.messages}
                      </Link>
                      <button
                        onClick={() => setRestaurantToDelete({ id: restaurant.id, name: restaurant.nameJp })}
                        className="flex items-center gap-1.5 px-3 py-2 text-sm text-red-600 border border-red-200 rounded-xl hover:bg-red-50 transition-colors ml-auto"
                      >
                        <Trash2 className="w-4 h-4" />
                        {t.ownerList.deleteBtn || "Xóa"}
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Add new restaurant CTA */}
        <div className="mt-6">
          <Link
            to="/owner/register"
            className="flex items-center justify-center gap-3 w-full py-4 border-2 border-dashed border-blue-200 rounded-2xl text-blue-500 hover:bg-blue-50 hover:border-blue-300 transition-all"
          >
            <Plus className="w-5 h-5" />
            <span className="text-sm">{t.ownerList.addNew}</span>
          </Link>
        </div>
      </div>

      {/* Delete Confirmation Modal */}
      {restaurantToDelete && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl">
            <div className="flex items-center gap-3 text-red-600 mb-4">
              <div className="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center shrink-0">
                <AlertCircle className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-medium text-gray-900">
                {t.ownerList.deleteConfirmTitle || "Xóa vĩnh viễn quán?"}
              </h3>
            </div>
            
            <p className="text-sm text-gray-600 mb-6">
              Bạn có chắc chắn muốn xóa vĩnh viễn quán <span className="font-semibold text-gray-900">{restaurantToDelete.name}</span> không? Toàn bộ dữ liệu về thực đơn, đánh giá, và hình ảnh sẽ bị xóa sạch và KHÔNG THỂ khôi phục.
            </p>

            <div className="flex items-center justify-end gap-3">
              <button
                onClick={() => setRestaurantToDelete(null)}
                disabled={isDeleting}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-xl hover:bg-gray-200 transition-colors disabled:opacity-50"
              >
                {t.common?.cancel || "Hủy"}
              </button>
              <button
                onClick={handleDelete}
                disabled={isDeleting}
                className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-xl hover:bg-red-700 transition-colors disabled:opacity-50"
              >
                {isDeleting ? (
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                ) : (
                  <Trash2 className="w-4 h-4" />
                )}
                {isDeleting ? "Đang xóa..." : "Xóa vĩnh viễn"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
