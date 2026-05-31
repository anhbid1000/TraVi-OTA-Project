type PartnerTopbarProps = {
  title: string;
  subtitle?: string;
  searchValue?: string;
  onSearchChange?: (value: string) => void;
  searchPlaceholder?: string;
};

export function PartnerTopbar({ title, subtitle, searchValue, onSearchChange, searchPlaceholder }: PartnerTopbarProps) {
  return (
    <header className="sticky top-0 z-40 flex h-16 items-center justify-between border-b border-outline-variant/30 bg-surface/70 px-5 backdrop-blur-md md:px-6">
      <div>
        <h2 className="font-display text-2xl font-bold text-primary">{title}</h2>
        {subtitle && <p className="text-sm text-on-surface-variant">{subtitle}</p>}
      </div>
      {onSearchChange && (
        <div className="hidden sm:flex items-center rounded-full border border-outline-variant/50 bg-white px-4 py-2 focus-within:ring-2 focus-within:ring-primary/20">
          <span className="material-symbols-outlined mr-2 text-outline">search</span>
          <input
            className="w-56 border-0 bg-transparent p-0 text-sm outline-none"
            placeholder={searchPlaceholder || 'Tìm kiếm...'}
            value={searchValue || ''}
            onChange={(e) => onSearchChange(e.target.value)}
          />
        </div>
      )}
    </header>
  );
}
